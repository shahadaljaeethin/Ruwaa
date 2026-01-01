package org.example.ruwaa.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Model.Customer;
import org.example.ruwaa.Service.CustomerService;
import org.example.ruwaa.Service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController
{
    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/get")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(customerService.findAll());
    }




}
