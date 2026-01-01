package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.DTOs.*;
import org.example.ruwaa.Config.JWT.JwtUtil;
import org.example.ruwaa.Model.Categories;
import org.example.ruwaa.Model.Customer;
import org.example.ruwaa.Model.Expert;
import org.example.ruwaa.Model.Users;
import org.example.ruwaa.Repository.CategoriesRepository;
import org.example.ruwaa.Repository.CustomerRepository;
import org.example.ruwaa.Repository.ExpertRepository;
import org.example.ruwaa.Repository.UsersRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService  {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomerRepository customerRepository;
    private final ExpertRepository expertRepository;
    private final CategoriesRepository categoriesRepository;

    public List<Users> getAllUsers(){return usersRepository.findAll();}


    public AuthResponse admin(Users admin){
        if (usersRepository.findUserByUsername(admin.getUsername()).isPresent()){
            throw new ApiException("username is already taken");
        }
        admin.setAbout_me("Ruwaa team");
        admin.setRole("ADMIN");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setBalance(0.0);
        usersRepository.save(admin);
        String token = jwtUtil.generateToken(admin);
        return new AuthResponse(token, admin.getUsername(), admin.getRole());
    }

    public AuthResponse login(AuthRequest auth){
        Users u = usersRepository.findUserByUsername(auth.getUsername()).orElseThrow(() -> new ApiException("wrong username"));

        if (!passwordEncoder.matches(auth.getPassword(), u.getPassword())){
            throw new ApiException("wrong password");
        }

        String token = jwtUtil.generateToken(u);

        return new AuthResponse(token, u.getUsername(), u.getRole());
    }

    public AuthResponse expertSignUp(RegisterExpertRequest auth){
        if (!isUnique(auth.getUsername(), auth.getEmail() )){
            throw new ApiException("username or email already exists");
        }
        Categories c = categoriesRepository.findCategoryByName(auth.getCategory()).orElseThrow(() -> new ApiException("no category have this name"));
        Users u = new Users();
        u.setUsername(auth.getUsername());
        u.setEmail(auth.getEmail());
        u.setName(auth.getName());
        u.setPassword(passwordEncoder.encode(auth.getPassword()));
        u.setPhone(auth.getPhone_number());
        u.setCreatedAt(LocalDateTime.now());
        u.setAbout_me("Hello I am new!");
        u.setBalance(0.0);
        u.setCreatedAt(LocalDateTime.now());
        u.setRole("EXPERT");
        usersRepository.save(u);

        Expert e = new Expert();
        e.setLinkedin_url(auth.getLinkedin_url());
        e.setIsActive(false);
        e.setIsAvailable(false);
        e.setCategory(c);
        e.setLinkedin_url(auth.getLinkedin_url());
        e.setConsult_price(0.0);
        e.setUsers(u);
        e.setCount_rating(0.0);
        e.setTotal_rating(0.0);
        if (auth.getC_price() == null){e.setReview_count(0);}
        e.setConsult_price(auth.getC_price());
        if(auth.getData()!=null) e.setData(auth.getData());

        expertRepository.save(e);
        return new AuthResponse(jwtUtil.generateToken(u), u.getUsername(), u.getRole());
    }

    public Users Me(String username){
       return usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
    }

    public void updateExpertProfile(String username, updateExpertRequest request){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if (!u.getRole().equals("EXPERT")){
            throw new ApiException("you are not an expert");
        }
        u.setAbout_me(request.getAbout_me());
        u.setUsername(request.getUsername());
        u.setPhone(request.getPhone_number());
        u.setEmail(request.getEmail());
        usersRepository.save(u);
        Expert e = u.getExpert();
        e.setLinkedin_url(request.getLinkedin_url());
        expertRepository.save(e);
    }

    public void updateCustomerProfile(String username, Users user){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if (!u.getRole().equals("CUSTOMER")){
            throw new ApiException("you are not a customer");
        }
        u.setEmail(user.getEmail());
        u.setUsername(user.getUsername());
        u.setPhone(user.getPhone());
        u.setAbout_me(user.getAbout_me());
        usersRepository.save(u);
    }


    public AuthResponse registerCustomer(RegisterCustomerRequest auth){
        System.out.println("service:"+auth);
        if (!isUnique(auth.getUsername(), auth.getEmail() )){
            throw new ApiException("username or email already exists");
        }

        Users u = new Users();
        u.setUsername(auth.getUsername());
        u.setEmail(auth.getEmail());
        u.setName(auth.getName());
        u.setAbout_me("Hello I am new!");
        u.setPassword(passwordEncoder.encode(auth.getPassword()));
        u.setCreatedAt(LocalDateTime.now());
        u.setRole("CUSTOMER");
        u.setBalance(0.0);
        u.setPhone(auth.getPhone_number());
        usersRepository.save(u);
        Customer c = new Customer();
        c.setUsers(u);

        customerRepository.save(c);
        return new AuthResponse(jwtUtil.generateToken(u), u.getUsername(), u.getRole());
    }



    public boolean isUnique(String username, String email){
        if (usersRepository.findUserByUsername(username).isPresent() || usersRepository.findUserByEmail(email).isPresent()){
            return false;
        }
        return true;
    }
}
