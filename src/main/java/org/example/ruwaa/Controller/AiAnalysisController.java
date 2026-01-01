package org.example.ruwaa.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Service.AiAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;


    @GetMapping("/analyze-customer-skills/{customerId}")
    public ResponseEntity<?> analyzeCustomerSkills (@PathVariable Integer customerId) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeCustomerSkills(customerId));
    }
}
