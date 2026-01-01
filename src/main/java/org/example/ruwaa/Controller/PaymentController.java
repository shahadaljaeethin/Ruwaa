package org.example.ruwaa.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController
{
    private final PaymentService paymentService;

    @GetMapping("/thanks")
    public ResponseEntity<?> paymentProcessedSuccessfully(){
        return ResponseEntity.status(200).body(new ApiResponse("Thank you"));
    }
}
