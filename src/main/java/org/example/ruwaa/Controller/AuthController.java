package org.example.ruwaa.Controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.DTOs.AuthRequest;
import org.example.ruwaa.DTOs.RegisterCustomerRequest;
import org.example.ruwaa.DTOs.RegisterExpertRequest;
import org.example.ruwaa.DTOs.updateExpertRequest;
import org.example.ruwaa.Model.Users;
import org.example.ruwaa.Service.AiService;
import org.example.ruwaa.Service.AuthService;
import org.example.ruwaa.Service.SendMailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<?> login (@RequestBody AuthRequest auth){
        return ResponseEntity.status(200).body(authService.login(auth));
    }

    @PostMapping("/signup/expert")
    public ResponseEntity<?> signupExpert ( @RequestBody @Valid RegisterExpertRequest auth){
        return ResponseEntity.status(200).body(authService.expertSignUp(auth));
    }

    @PostMapping("/signup/customer")
    public ResponseEntity<?> signupCustomer (@RequestBody @Valid RegisterCustomerRequest auth){
        return ResponseEntity.status(200).body(authService.registerCustomer(auth));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> admin (@RequestBody @Valid Users admin){
        return ResponseEntity.status(200).body(authService.admin(admin));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth){
        return ResponseEntity.status(200).body(authService.Me(auth.getName()));
    }

    @PutMapping("/update-expert")
    public ResponseEntity<?> updateMyExpert (Authentication auth, @RequestBody @Valid updateExpertRequest update){
        authService.updateExpertProfile(auth.getName(),update);
        return ResponseEntity.status(200).body(new ApiResponse("profile updated successfully"));
    }

    @PutMapping("/update-customer")
    public ResponseEntity<?> updateMyCustomer(Authentication auth, @RequestBody @Valid Users update){
        authService.updateCustomerProfile(auth.getName(),update);
        return ResponseEntity.status(200).body(new ApiResponse("profile updated successfully"));
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(authService.getAllUsers());
    }
}
