package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.DTOs.LearningContentDTO;
import org.example.ruwaa.DTOs.ReviewAIdto;
import org.example.ruwaa.DTOs.WorkPostDTO;
import org.example.ruwaa.Model.*;
import org.example.ruwaa.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService
{
    private final CategoriesRepository categoriesRepository;
    private final PostRepository postRepository;
    private final UsersRepository usersRepository;
    private final CustomerRepository customerRepository;
    private final AiService aiService;
    private final ExpertRepository expertRepository;
    private final AttachmentsRepository attachmentsRepository;


    public List<Post> getAll(){
        List<Post> postList = postRepository.findAll();
        if (postList.isEmpty()){
            throw new ApiException("no posts found");
        }
        return postList;
    }



    public void addWorkPost(String username,MultipartFile attachement, String content, Boolean isPublic, String postCategory) throws IOException {
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if(!u.getRole().equals("CUSTOMER")) throw new ApiException("only customers can add this content type");
        Categories category = categoriesRepository.findCategoryByName(postCategory).orElseThrow(() -> new ApiException("invalid category"));

        String type = (isPublic) ? "public_work":"private_work";
        List<Users> permitPrivateWorkVisiablity;
        if(isPublic) permitPrivateWorkVisiablity=null;
        else {
            permitPrivateWorkVisiablity= new ArrayList<Users>();
            permitPrivateWorkVisiablity.add(u); //adding the current user so they can see their own work :)
        }

        Post p = new Post();
        p.setPublishAt(LocalDateTime.now());
        p.setCategory(category);
        p.setUsers(u);
        p.setContent(content);
        p.setType(type);
        p.setViews(0);
        p.setPermitWorkVisiablity(permitPrivateWorkVisiablity);
        u.getRequestedPrivateWorks().add(p);
        postRepository.save(p);
        Attachments a = new Attachments();
        a.setPost(p);
        a.setData(attachement.getBytes());
        a.setName(attachement.getOriginalFilename());
        a.setType(attachement.getContentType());
        attachmentsRepository.save(a);
    }


    public void addLearningContent(String username,MultipartFile image,String content, Boolean isFree) throws IOException {
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if(!u.getRole().equals("EXPERT")) throw new ApiException("only expert can add this content type");
        Expert e = expertRepository.findExpertById(u.getId()).orElseThrow();
        if(!e.getIsActive()) throw new ApiException("expert account not activated yet");
        String type = (isFree) ? "free_content":"subscription_content";


        Post post = new Post();
        post.setPublishAt(LocalDateTime.now());
        post.setUsers(u);
        post.setContent(content);
        post.setType(type);
        post.setViews(0);
        post.setCategory(u.getExpert().getCategory());
        postRepository.save(post);
        Attachments a = new Attachments();
        a.setPost(post);
        a.setData(image.getBytes());
        a.setName(image.getOriginalFilename());
        a.setType(image.getContentType());
        attachmentsRepository.save(a);



    }

    public void updateWorkPost(String username, Integer id, WorkPostDTO dto){
        Users u  = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Post post = postRepository.findPostById(id).orElseThrow(() -> new ApiException("post not found"));
        if (u != post.getUsers()){
            throw new ApiException("only post author can update it");
        }
        post.setContent(dto.getContent());
        post.setAttachment(dto.getAttachments());

        postRepository.save(post);
    }
    public void updateLearningCont(String username, Integer id, LearningContentDTO dto)  {
        Users u  = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Post post = postRepository.findPostById(id).orElseThrow(() -> new ApiException("post not found"));
        if (u != post.getUsers()){
            throw new ApiException("only post author can update it");
        }
        post.setContent(dto.getContent());
        post.setAttachment(dto.getAttachments());

        postRepository.save(post);
    }

    public void deletePost(String username, Integer id){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Post m = postRepository.findPostById(id).orElseThrow(() -> new ApiException("post not found"));
        if (u != m.getUsers()){
            throw new ApiException("only post author can remove it");
        }
        postRepository.delete(m);
    }


    public Post viewWorkPost(String username ,Integer postId){
        Post p = postRepository.findPostById(postId).orElseThrow(() -> new ApiException("post not found"));
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
    if (p.getType().contains("content")){
        throw new ApiException("post was not found");
    }
    if(p.getType().equals("private_work"))
     if(!p.getPermitWorkVisiablity().contains(user)) throw new ApiException("This is private work");
    p.setViews(p.getViews()+1);
    postRepository.save(p);
    return p;
    }

    public Post viewLearningPost(String username ,Integer postId){
        Post p = postRepository.findPostById(postId).orElseThrow(() -> new ApiException("post not found"));
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        if (p.getType().contains("work")){
            throw new ApiException("post was not found");
        }
        if(p.getType().equals("subscription_content")) {

            if (user.getRole().equals("CUSTOMER")) {
                Customer customer = user.getCustomer();
                if (customer.getSubscription() == null || customer.getSubscription().getEnd_date().isBefore(LocalDateTime.now()))
                    throw new ApiException("this content needs subscription");

            }
            else if(!p.getUsers().equals(user)&&user.getRole().equals("EXPERT")) throw new ApiException("this content for customer");

        } //sub

        p.setViews(p.getViews()+1);
        postRepository.save(p);
        return p;
    }


    public List<Post> freeFeed(){
        return postRepository.findPostByType("free_content");
    }


    public List<Post> subscribeFeed(String username){
        Users users = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        if(users.getRole().equals("EXPERT")) throw new ApiException("subscription content feed allowed for customers only");
        if(!users.getRole().equals("ADMIN")){
            Customer customer = users.getCustomer();
            if (customer.getSubscription() == null || customer.getSubscription().getEnd_date().isBefore(LocalDateTime.now()))
                throw new ApiException("this feed needs subscription");
        }
        return postRepository.findPostByType("subscription_content");
    }


    public List<Post> workFeed(){
        return postRepository.findPostByType("public_work");
    }





    public List<Post> getMyPost(String username){
        Users user = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("Account not found"));
        return postRepository.findPostByUsers_id(user.getId());
    }

    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start == -1 || end == -1) {
            throw new RuntimeException("No JSON array found in AI response");
        }
        return response.substring(start, end + 1);
    }

    public ArrayList<ReviewAIdto> reviewMyWork(String username, Integer postId){
        Users u =  usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Post p = postRepository.findPostById(postId).orElseThrow(() -> new ApiException("post not found"));
        if (u != p.getUsers()){
            throw new ApiException("only post author can review it using AI");
        }
        if (u.getCustomer().getSubscription() == null || u.getCustomer().getSubscription().getEnd_date().isBefore(LocalDateTime.now()) && u.getExpert() == null){
            throw new ApiException("this content needs subscription");
        }
        if(!p.getType().equals("public_work")&&!p.getType().equals("private_work")) throw new ApiException("this is not work post");



          String dtoString = "You are a professional critic and expert.You will be provided with a work that may be one of the following types:(programming code, literary text, poetry, artwork, design, or any other creative work).Requirements:1. Automatically identify the type of work based on its content.2. Select the appropriate standard evaluation criteria for this field (such as clarity, structure, creativity, details, performance, style, depending on the type of work).3. Evaluate the work in a professional and balanced manner.The output must be JSON only (with no additional text),formatted as a list (array) of objects, where each object represents exactly one criterion, using the following structure:{  \"rate\": \"Numeric rating from 1 to 5 (can be decimal such as 3.8/5)\",  \"criteria\": \"Name of the criterion\",  \"comment\": \"What the work achieved under this criterion\",  \"suggestion\": \"What can be improved or constructive feedback\"}Strict conditions:- Do not commit to a fixed number of criteria; choose the appropriate number based on the nature of the work.- Use formal and professional Arabic.- Do not use emojis. -Do not add any explanation outside the JSON. - Make the evaluation realistic and non-flattering. The work to be analyzed: "
;
        ObjectMapper mapper = new ObjectMapper();
        try {
            dtoString += aiService.dtoPost(p);

         String aiResponse = aiService.askAI(dtoString);
        aiResponse = extractJsonArray(aiResponse);
            List<ReviewAIdto> parsed =
                    mapper.readValue(aiResponse, new TypeReference<List<ReviewAIdto>>() {});
            return new ArrayList<>(parsed);

        } catch (Exception e) {
            System.out.println("AI:"+e.getMessage());
            return new ArrayList<>();
        }

    }

    public void changeVisibilityToPublic(String username, Integer postId){
        Users u =  usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Post p = postRepository.findPostById(postId).orElseThrow(() -> new ApiException("post not found"));
        if (u != p.getUsers()){
            throw new ApiException("only the post`s author can change visibility");
        }
        if(!p.getType().equals("public_work")&&!p.getType().equals("private_work")) throw new ApiException("this is not work post");
        if(p.getType().equals("public_work")) throw new ApiException("this work is already Public");

        p.setType("public_work");
        postRepository.save(p);

    }


    public void changeVisibilityToPrivate(String username, Integer postId){
        Users u =  usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Post p = postRepository.findPostById(postId).orElseThrow(() -> new ApiException("post not found"));
        if(!p.getType().equals("public_work")&&!p.getType().equals("private_work")) throw new ApiException("this is not work post");
        if(p.getType().equals("private_work")) throw new ApiException("this work is already Private");
        if (u != p.getUsers()){
            throw new ApiException("only the post`s author can change visibility");
        }
        p.setType("private_work");
        postRepository.save(p);

    }



}
