package org.example.ruwaa.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Model.Expert;
import org.example.ruwaa.Model.Review;
import org.example.ruwaa.Service.ExpertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/expert")
@RequiredArgsConstructor
public class ExpertController
{
    private final ExpertService expertService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(expertService.getAll());
    }


    @GetMapping("/most-active/category/{category}")
    public ResponseEntity<?> findMostActiveExpertByCategory(Authentication auth, @PathVariable String category)
    {
        return ResponseEntity.status(200).body(expertService.findMostActiveExpertByCategory(auth.getName(), category));
    }


    @GetMapping("/get-expert-by-category/{category}")
    public ResponseEntity<?> getExpertByCategory (Authentication auth, @PathVariable String category) {
        return ResponseEntity.status(200).body(expertService.getExpertByCategory(auth.getName(), category));
    }


    @PutMapping("/discount/{discountPercentage}/{days}")
    public ResponseEntity<?> applyDiscount(Authentication auth, @PathVariable Double discountPercentage, @PathVariable Integer days) {

        expertService.applyDiscount(auth.getName(), discountPercentage,days);
        return ResponseEntity.status(200).body(new ApiResponse("Discount applied successfully"));
    }


    @GetMapping("/get-high-rated-by-category/{category}")
    public ResponseEntity<?> getHighRatedExpertByCategory (Authentication auth, @PathVariable String category) {
        return ResponseEntity.status(200).body(expertService.getHighRatedExpertByCategory(auth.getName(), category));
    }


    @GetMapping("/calculate-average/{someExpert}")
    public ResponseEntity<?> getExpertRateAverage(Authentication auth, @PathVariable Integer someExpert){
    return ResponseEntity.status(200).body(new ApiResponse(""+(expertService.getExpertRateAverage(auth.getName(), someExpert))+"/5"));
    }


    @PutMapping("/subscription-earning/{earningMonth}/{views}")
    public ResponseEntity<?> subscriptionEarning(@PathVariable Double earningMonth, @PathVariable Integer views){
    return ResponseEntity.status(200).body(new ApiResponse(expertService.subscriptionEarning(earningMonth,views)));
    }


    @PutMapping("/activate/{expert}")
    public ResponseEntity<?> activateExp(@PathVariable Integer expert){
        expertService.activateExpert(expert);
        return ResponseEntity.status(200).body(new ApiResponse("activate complete"));
    }


    @DeleteMapping("/reject/{expert}")
    public ResponseEntity<?> rejectExp(@PathVariable Integer expert){
        expertService.rejectExpert(expert);
        return ResponseEntity.status(200).body(new ApiResponse("reject complete, email sent"));
    }


    @PutMapping("/available")
    public ResponseEntity<?> setStatAvailable(Authentication auth){
        expertService.setAvailable(auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("Status set to available"));
    }


    @PutMapping("/busy")
    public ResponseEntity<?> setStatBusy(Authentication auth){
        expertService.setBusy(auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("Status set to busy"));
    }

    @PutMapping("/change-consultation-price/{amount}")
    public ResponseEntity<?> changeConsultationPrice(Authentication auth,@PathVariable Double amount){
        expertService.changeConsultPrice(auth.getName(), amount);
        return ResponseEntity.status(200).body(new ApiResponse("change consultation price successful"));
    }
}
