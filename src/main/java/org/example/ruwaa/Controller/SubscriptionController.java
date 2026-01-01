package org.example.ruwaa.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController
{
    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe/month")
    public ResponseEntity<?> subscribe(Authentication auth){
        System.out.println(auth.getName());
        subscriptionService.subscribe(auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("thank you for subscribing"));
    }

    @PostMapping("/subscribe/3month")
    public ResponseEntity<?> subscribeThreeMonths(Authentication auth){
        System.out.println(auth.getName());
        subscriptionService.subscribeThreeMonths(auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("thank you for subscribing"));
    }

    @PostMapping("/subscribe/6month")
    public ResponseEntity<?> subscribeSixMonths(Authentication auth){
        System.out.println(auth.getName());
        subscriptionService.subscribeSixMonths(auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("thank you for subscribing"));
    }

    @GetMapping("/my-subscription")
    public ResponseEntity<?> getMySubscription(Authentication auth){
        return ResponseEntity.status(200).body(subscriptionService.getSubscription(auth.getName()));
    }

    @PostMapping("/gift-subscription/to/{username}")
    public ResponseEntity<?> giftSubscription(@PathVariable String username, Authentication auth){
        subscriptionService.giftSubscribe(username, auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("gift subscription sent to: " + username + " successfully"));
    }

    @PostMapping("/gift-3subscription/to/{username}")
    public ResponseEntity<?> giftThreeSubscription(@PathVariable String username, Authentication auth){
        subscriptionService.giftThreeSubscribe(username, auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("gift subscription sent to: " + username + " successfully"));
    }

    @PostMapping("/gift-6subscription/to/{username}")
    public ResponseEntity<?> giftSixSubscription(@PathVariable String username, Authentication auth){
        subscriptionService.giftSixSubscribe(username, auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("gift subscription sent to: " + username + " successfully"));
    }
}
