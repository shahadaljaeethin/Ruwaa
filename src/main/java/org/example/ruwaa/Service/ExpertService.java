package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.*;
import org.example.ruwaa.Repository.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExpertService
{
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ExpertRepository expertRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    private final SendMailService sendMailService;

    public List<Expert> getAll(){
        List<Expert> experts = expertRepository.findAll();
        if (experts.isEmpty()){
            throw new ApiException("expert not found");
        }
        return experts;
    }

    public void update(Integer id, Users user){
        Expert e = expertRepository.findExpertById(id).orElseThrow(() -> new ApiException("expert not found"));

        Users u = e.getUsers();
        u.setUsername(user.getUsername());
        u.setPassword(user.getPassword());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        u.setName(user.getName());
        e.setUsers(u);
        expertRepository.save(e);
    }

    public void delete(Integer id){
        Expert e = expertRepository.findExpertById(id).orElseThrow(() -> new ApiException("expert not found"));

        expertRepository.delete(e);
    }

    public Expert findMostActiveExpertByCategory(String username, String category){
        return expertRepository.findMostActiveExpert(category).orElseThrow(() -> new ApiException("no experts added to this category"));
    }


    public List<Expert> getExpertByCategory (String username, String category) {
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Categories categories = categoriesRepository.findCategoryByName(category).orElseThrow(()-> new ApiException("user not found"));
        List<Expert> experts = expertRepository.findExpertByCategory(categories);

        for (Expert e : experts) {
            e.setConsult_price(e.getConsult_price()+(e.getConsult_price()*0.2));
        }
        if (experts.isEmpty()) {
            throw new ApiException("No experts found for this category");
        }

        return experts;
    }



    public void applyDiscount(String username, Double discountPercentage,Integer days) {
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Expert expert = expertRepository.findExpertById(user.getId()).orElseThrow(() -> new ApiException("Expert not found"));

        if(days<1) throw new ApiException("invalid date");
        Double originalPrice = expert.getConsult_price();
        double newPrice = originalPrice * (1 - discountPercentage / 100);
        expert.setConsult_price(newPrice);
        expertRepository.save(expert);


        scheduler.schedule(() -> {
            Expert e = expertRepository.findExpertById(expert.getId()).orElse(null);
            if (e != null) {
                e.setConsult_price(originalPrice);
                expertRepository.save(e);
            }
        }, days, TimeUnit.DAYS);

    }


    public Double getExpertRateAverage(String username, Integer expertId){
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Expert expert = expertRepository.findExpertById(expertId).orElseThrow(() -> new ApiException("expert not found"));
        if(expert.getCount_rating()==0) return 1.0; //minimum star is 1
        return expert.getTotal_rating()/expert.getCount_rating();
    }


    public Expert getHighRatedExpertByCategory (String username, String category) {
        Users user = usersRepository.findUserByUsername(username).orElseThrow(()-> new ApiException("user not found"));
        Categories categories = categoriesRepository.findCategoryByName(category).orElseThrow(() -> new ApiException("Category not found"));
        List<Expert> experts = expertRepository.findExpertByCategory(categories);
        if (experts.isEmpty()) {
            throw new ApiException("No experts found for this category");
        }

        Double high = 0.0;
        Expert expert = new Expert();

        for (Expert expert1 : experts) {
            if (getExpertRateAverage(username,expert1.getId()) >= high) {
                high = getExpertRateAverage(username, expert1.getId());
                expert = expert1;
            }
        }
        return  expert;
    }



    public String subscriptionEarning(Double earningMonth,Integer views){
    //for every *views in this  month, give these expert *earningMonth credit.
    List<Post> recentContent = postRepository.getRecentMonthLearningPosts(LocalDateTime.now().minusMonths(1),views);
    Double credit=0.0; //for every x views
    Integer countPost=0;

    for(Post post : recentContent){

    countPost++;
    credit = Math.floor(post.getViews() * 1.0 / views)*earningMonth;
    post.getUsers().setBalance(post.getUsers().getBalance()+credit);
    postRepository.save(post);

         }
    return "total of "+countPost+" learning content has achieved +"+views+"Views. Credit sent to expert successfully";
    }


    public void activateExpert(Integer expertId){
        Expert expert = expertRepository.findExpertById(expertId).orElseThrow(()-> new ApiException("expert not found"));
        if(expert.getIsActive()) throw new ApiException("this expert is already active");
        expert.setIsActive(true);
        expert.setIsAvailable(true);
        expertRepository.save(expert);
    }

    public void changeConsultPrice(String username,Double amount){
        Expert e = expertRepository.findExpertByUsername(username).orElseThrow(() -> new ApiException("username not found"));
        e.setConsult_price(amount);
        expertRepository.save(e);
    }


    public void rejectExpert(Integer expertId){
        Expert expert = expertRepository.findExpertById(expertId).orElseThrow(()-> new ApiException("expert not found"));
        if(expert.getIsActive()) throw new ApiException("you can't reject active expert");
        Users info = expert.getUsers();
        String title="Apology for Incomplete register";
        String body="hello dear "+info.getName()+",\n\nWe would like to sincerely apologize for the inconvenience caused due to the lack of sufficient information and the presence of details that did not fully match the required criteria.\n" +
                "\n" +
                "This situation does not reflect our standards, and we acknowledge the importance of providing accurate and complete information, especially in a professional and expert-driven context.\n" +
                "\n" +
                "We appreciate your understanding and patience, and we value your expertise greatly. Please accept our apologies, and rest assured that we are taking the necessary steps to avoid such issues in the future.\n" +
                "\n" +
                "Thank you for your time and consideration.\n" +
                "\n" +
                "Kind regards,\nRuwaa Team";
        sendMailService.sendMessage(info.getEmail(),title,body);
        usersRepository.delete(info);
    }


    public void setAvailable(String username){
        Expert expert = expertRepository.findExpertByUsername(username).orElseThrow(()-> new ApiException("expert not found"));
        if(expert.getIsAvailable()) throw new ApiException("status already available");
        expert.setIsAvailable(true);
        expertRepository.save(expert);
    }


    public void setBusy(String username){
        Expert expert = expertRepository.findExpertByUsername(username).orElseThrow(()-> new ApiException("expert not found"));
        if(!expert.getIsAvailable()) throw new ApiException("status already busy");
        expert.setIsAvailable(false);
        expertRepository.save(expert);
    }

}



