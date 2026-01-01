package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.DTOs.ReviewAIdto;
import org.example.ruwaa.DTOs.ReviewAssistance;
import org.example.ruwaa.DTOs.ReviewDTO;
import org.example.ruwaa.DTOs.TemplateDTO;
import org.example.ruwaa.Model.*;
import org.example.ruwaa.Repository.*;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReviewService
{
    private final ChatRepository chatRepository;

    private final ReviewRepository reviewRepository;
    private final PostRepository mediaRepository;
    private final ExpertRepository expertRepository;
    private final PostRepository postRepository;
    private final PaymentService paymentService;
    private final UsersRepository usersRepository;
    private final CustomerRepository customerRepository;
    private final SendMailService sendMailService;
    private final AiService aiService;
    private final CardsRepository cardsRepository;
    private final MessageRepository messageRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public List<Review> getAll(){
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()){
            throw new ApiException("no reviews found");
        }
        return reviews;
    }


    public void update(Integer id, Review review){
        Review r = reviewRepository.findReviewById(id).orElseThrow(() -> new ApiException("review not found"));

        r.setContent(review.getContent());
        reviewRepository.save(r);
    }

    public void delete(Integer id){
        Review r = reviewRepository.findReviewById(id).orElseThrow(() -> new ApiException("review not found"));

        reviewRepository.delete(r);
    }

    public List<Review> getFinishedReviews(String username){
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("expert not found"));

        List<Review> r = reviewRepository.findFinishedReviews();
        if (r.isEmpty()){
            throw new ApiException("there are no finished reviews");
        }
        return r;
    }

    public List<Review> getUnfinishedReviews(String username){
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("expert not found"));

        List<Review> r = reviewRepository.findUnfinishedReviews();
        if (r.isEmpty()){
            throw new ApiException("there are no unfinished reviews");
        }
        return r;
    }


    public void rateReview(Integer reviewId,Integer rate){
        Review review = reviewRepository.findReviewById(reviewId).orElseThrow(()-> new ApiException("review not found"));
        if(review.getHasRated()) throw new ApiException("you have rated this review before");
        if(rate>5||rate<1) throw new ApiException("Rate number should be between 1 and 5");
        review.setHasRated(true);
        review.setFeedback_rating(rate);
        reviewRepository.save(review);

        Expert expert = review.getExpert();

        //I need variable names
        expert.setTotal_rating(expert.getTotal_rating()+rate);
        expert.setCount_rating(expert.getCount_rating()+1);
        //expert.setReviewRate(expert.getSUM()/expert.getCounter());

        expertRepository.save(expert);

    }

     private Double refund=0.0;
    public void requestReview(String username, Integer expert_id,Integer workId){

        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("expert not found"));
        Expert e = expertRepository.findExpertById(expert_id).orElseThrow(() -> new ApiException("expert not found"));
        if(!e.getIsActive()) throw new ApiException("expert account not activated yet");
        if(!e.getIsAvailable()) throw new ApiException("expert is busy");
       // Double refund=0.0;

        Post p = mediaRepository.findPostById(workId).orElseThrow(() -> new ApiException("work not found"));
        if(!p.getType().equals("public_work")&&!p.getType().equals("private_work")) throw new ApiException("this is not work post");
        List<Card> c = cardsRepository.findCardsByUserId(p.getUsers().getId());
        if (c.isEmpty()){
            throw new ApiException("add a card first to order review");
        }
        Subscription s = p.getUsers().getCustomer().getSubscription();
        if(s == null || s.getEnd_date().isBefore(LocalDateTime.now())){
            Double amount = e.getConsult_price()+(e.getConsult_price()*0.2);
            paymentService.processPayment(amount,p.getUsers().getCards().get(0));
            refund=amount;
        }else {
            Double amount = e.getConsult_price()+(e.getConsult_price()*0.1);
            paymentService.processPayment(amount,p.getUsers().getCards().get(0));
            refund=amount;
        }
        Review review = new Review();
        review.setStatus("Pending"); ///****************Check regex
        review.setFeedback_rating(1);
        review.setHasRated(false);
        review.setPost(p);
        review.setExpert(e);
        expertRepository.save(e);
        reviewRepository.save(review);
        //notify expert         **update link later for better UX
        String body = "a new review request worth "+e.getConsult_price()+"SR,\n\n" + //**** كم نخلي نسبة الربح حقتنا عشان نطرح
                "View request link: localhost:8080/api/v1/post/view/work/"+p.getUsers().getId()+"\n\n" +
                "Work content: "+p.getContent();
        sendMailService.sendMessage(e.getUsers().getEmail(),"New Review Request From "+p.getUsers().getName(),body);
        //if 24H pass without accepting request, refund money
        scheduler.schedule(() -> {
            if(!review.getStatus().equals("Accepted")||!review.getStatus().equals("Completed")) {
                //if(user.getBalance()==null) user.setBalance(0.0);


               user.setBalance(user.getBalance()+refund);
               usersRepository.save(user);
               sendMailService.sendMessage(user.getEmail(),"Refund Successful","your request for rating "+e.getUsers().getUsername()+" didn't get approve for 24H. So, we refunded your money.");
                review.setStatus("Rejected");
                reviewRepository.save(review);
            }
        }, 24, TimeUnit.HOURS);

    }


    public void submitReview(Integer reviewId,String username ,ReviewDTO reviewDTO){
        Review review = reviewRepository.findReviewById(reviewId).orElseThrow(()-> new ApiException("review not found"));
        Users expert = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("expert not found"));
        if(!expert.getRole().equals("EXPERT")) throw new ApiException("unAuthorized writing review");

        Expert reviewer = reviewRepository.findExpertByReviewId(reviewId).orElseThrow(()-> new ApiException("expert of review not found"));
        if (expert != reviewer.getUsers()) {
            throw new ApiException("this review doesnt belong to you");
        }
        Expert expert1 = expertRepository.findExpertByUsername(username).orElseThrow(() -> new ApiException("expert not found"));

        if (!review.getStatus().equals("Accepted")){
            throw new ApiException("review not accepted yet");
        }
        review.setContent(reviewDTO.getContent());
        review.setStatus("Completed"); //***********CHECK REGEX




        Double credit = expert.getExpert().getConsult_price();

        expert.setBalance(expert.getBalance()+credit);

        expert.getExpert().setReview_count(expert1.getReview_count()+1);
        expertRepository.save(expert.getExpert());
        String message = "your review request to: "+expert.getName()+"\n saying: "+reviewDTO.getContent();
        sendMailService.sendMessage(review.getPost().getUsers().getEmail(),"review submitted",message);
    }


    public void acceptReview ( String username, Integer reviewId) {
        Users expert = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Review review = reviewRepository.findReviewById(reviewId).orElseThrow(() -> new ApiException("review not found"));
        Expert reviewer = reviewRepository.findExpertByReviewId(reviewId).orElseThrow(()-> new ApiException("expert of review not found"));
        if (expert != reviewer.getUsers()) {
            throw new ApiException("this review doesnt belong to you");
        }
        if(review.getStatus().equals("Accepted")||review.getStatus().equals("Completed")) throw new ApiException("you accepted or submited this review already");
        review.setStatus("Accepted");
        reviewRepository.save(review);
    }


    public void rejectReview (String username, Integer reviewId,String reason) {
        Users expert = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Review review = reviewRepository.findReviewById(reviewId).orElseThrow(() -> new ApiException("review not found"));
        Expert reviewer = reviewRepository.findExpertByReviewId(reviewId).orElseThrow(()-> new ApiException("expert of review not found"));
        if (expert != reviewer.getUsers()) {
            throw new ApiException("this review doesn't belong to you");
        }
        if (review.getStatus().equals("Completed")) {
            throw new ApiException("this review is already completed");
        }
        if (reason != null || !reason.equals("") ) {
            String message = "Dear "+review.getPost().getUsers().getName()+"\n\n the request to review your post\n"+review.getPost().getContent()+ " \n were rejected by :"+expert.getName()+" for the following reason: "+reason;
            sendMailService.sendMessage(review.getPost().getUsers().getEmail(),"review rejected",message);
        }
        String message = "Dear "+review.getPost().getUsers().getName()+"\n\n the request to review your post\n"+review.getPost().getContent()+ " \n were rejected by :"+expert.getName();
        sendMailService.sendMessage(review.getPost().getUsers().getEmail(),"review rejected",message);

        review.setStatus("Rejected");
        reviewRepository.save(review);
    }


    public void rejectAll (String username) {
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Expert expert = expertRepository.findExpertById(user.getId()).orElseThrow(()-> new ApiException("expert not found"));

        List<Review> reviews = reviewRepository.findAllByExpert(expert);

        for (Review review : reviews) {

            if (review.getStatus().equals("Pending")) {
                review.setStatus("Rejected");
                reviewRepository.save(review);
            }
        }
    }


    public List<Review> getPendingReviews (String username) {
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Expert expert = expertRepository.findExpertById(user.getId()).orElseThrow(()-> new ApiException("expert not found"));

        List<Review> reviews = reviewRepository.findPendingReviews(expert);

        if (reviews.isEmpty()) {
            throw new ApiException("No pending reviews found");
        }
        return reviews;
    }


    public List<Review> getReviewsRequest (String username) {
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Expert expert = expertRepository.findExpertById(user.getId()).orElseThrow(() -> new ApiException("Expert not found"));

        List<Review> reviews = reviewRepository.findAllByExpert(expert);
        if (reviews.isEmpty()) {
            throw new ApiException("No reviews found");
        }
        return reviews;
    }


    public List<Review> getSentRequests(String username) {
        Users customer = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));

        List<Review> customerRequests = new ArrayList<>();
        List<Review> all = reviewRepository.findAll();

        for (Review review : all) {
            if (review.getPost().getUsers().getId().equals(customer.getId())){
                customerRequests.add(review);
            }
        }

        if (customerRequests.isEmpty()) {
            throw new ApiException("No send request found");
        }
        return customerRequests;
    }

    public List<Review> getCompletedReviewsByPost(Integer postId, String username) {
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("Customer not found"));
        Post p = postRepository.findPostById(postId).orElseThrow(() -> new ApiException("Post not found"));
        if (p.getUsers() != c.getUsers()){
            throw new ApiException("this post doesnt belong to you");
        }
        List<Review>  completed = reviewRepository.findCompletedReviewsOfPost(postId);

        if (completed.isEmpty()) {
            throw new ApiException("No reviews found");
        }
        return completed;
    }
    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start == -1 || end == -1) {
            throw new RuntimeException("No JSON array found in AI response");
        }
        return response.substring(start, end + 1);
    }
    public ArrayList<TemplateDTO> makeReviewTemplate(String username, Integer reviewId){
        Expert expert = expertRepository.findExpertByUsername(username).orElseThrow(() -> new ApiException("Customer not found"));

        Review review = reviewRepository.findReviewById(reviewId).orElseThrow(()->new ApiException("review not found"));
        if(!review.getExpert().equals(expert)) throw new ApiException("unAuthorized");
        Post p =  review.getPost();
        if(!p.getType().equals("public_work")&&!p.getType().equals("private_work")) throw new ApiException("this is not work post");

        //AI
        //String prompt = "You are a professional critic and expert will help other critic .You will be provided with a work that may be one of the following types:(programming code, literary text, poetry, artwork, design, or any other creative work).Requirements:1. Automatically identify the type of work based on its content.2. Select the appropriate standard evaluation criteria for this field (such as clarity, structure, creativity, details, performance, style, depending on the type of work). Strict conditions:- Do not commit to a fixed number of criteria; choose the appropriate number based on the nature of the work.- Use formal and professional Arabic.- Do not use emojis. -Do not add any explanation or extra - Make the evaluation realistic and non-flattering.   expected answer example (1.الأصالة والإبداع) The work to be analyzed: ";
        String prompt = "You are a professional critic and expert. Your task is to identify ONLY the standard evaluation criteria titles suitable for the given work. Return ONLY a valid JSON array where each element contains exactly one field named 'criteria'. Do NOT include explanations, ratings, comments, markdown, backticks, or extra text. Use formal Arabic. Example format: [{\"criteria\":\"1.الوضوح\"}]. The work to be analyzed: ";

        prompt+=aiService.dtoPost(p);
        String result =  aiService.askAI(prompt);
         result =  extractJsonArray(result);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<TemplateDTO> parsed =
                    mapper.readValue(result, new TypeReference<List<TemplateDTO>>() {});
            return new ArrayList<>(parsed);

        } catch (Exception e) {
            System.out.println("AI:"+e.getMessage());
            return new ArrayList<>();
        }


    }

    public ArrayList<ReviewAssistance> reviewAssistance(String username,Integer reviewId){
        Expert expert = expertRepository.findExpertByUsername(username).orElseThrow(() -> new ApiException("Customer not found"));

        Review review = reviewRepository.findReviewById(reviewId).orElseThrow(()->new ApiException("review not found"));
        if(!review.getExpert().equals(expert)) throw new ApiException("unAuthorized");
        Post p =  review.getPost();
        if(!p.getType().equals("public_work")&&!p.getType().equals("private_work")) throw new ApiException("this is not work post");

        //AI
        String prompt = "You are a professional critic and expert.You will be provided with a work that may be one of the following types:(programming code, literary text, poetry, artwork, design, or any other creative work).Requirements:1. Automatically identify the type of work based on its content.2. Select the appropriate standard evaluation criteria for this field (such as clarity, structure, creativity, details, performance, style, depending on the type of work). 3.Provided helpful tools af any. The output must be JSON only (with no additional text),formatted as a list (array) of objects, where each object represents exactly one criterion, using the following structure:{   \"criteria\": \"Name of the criterion\",  \"helpfulQues\": \"What question might help any critic to rate the criterion \",  \"tools\": \"helpful tools for rating, if any or says none\"}Strict conditions:- Do not commit to a fixed number of criteria; choose the appropriate number based on the nature of the work.- Use formal and professional Arabic.- Do not use emojis. -Do not add any explanation outside the JSON. - Make the evaluation realistic and non-flattering. The work to be analyzed: ";
        prompt+=aiService.dtoPost(p);

        ObjectMapper mapper = new ObjectMapper();
        try {


            String aiResponse = aiService.askAI(prompt);
            aiResponse = extractJsonArray(aiResponse);
            List<ReviewAssistance> parsed =
                    mapper.readValue(aiResponse, new TypeReference<List<ReviewAssistance>>() {});
            return new ArrayList<>(parsed);

        } catch (Exception e) {
            System.out.println("AI:"+e.getMessage());
            return new ArrayList<>();
        }


    }





}

