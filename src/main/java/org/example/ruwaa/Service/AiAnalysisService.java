package org.example.ruwaa.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.DTOs.CustomerReviewAnalysisDTO;
import org.example.ruwaa.Model.Review;
import org.example.ruwaa.Repository.ReviewRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AiAnalysisService {

    private final ReviewRepository reviewRepository;
    private final OpenAiService openAiService;


    public CustomerReviewAnalysisDTO analyzeCustomerSkills(Integer customerId) {
        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getPost().getUsers().getId().equals(customerId))
                .collect(Collectors.toList());

        if (reviews.isEmpty()) {
            throw new ApiException("No reviews found for this customer");
        }

        String combinedReviews = reviews.stream()
                .map(Review::getContent)
                .collect(Collectors.joining("\n"));

        String prompt = """
                Analyze the following reviews written for a user. 
                Extract the following in JSON format:
                - skills: list of skills mentioned
                - strengths: list of strengths
                - weaknesses: list of weaknesses
                - overall_rating: number from 1 to 5

                Reviews:
                %s
                """.formatted(combinedReviews);

        String content = openAiService.analyzeText(prompt);

        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("OpenAI response does not contain valid JSON: " + content);
        }
        String jsonPart = content.substring(start, end + 1);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonPart, CustomerReviewAnalysisDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse OpenAI JSON content: " + jsonPart, e);
        }
    }
}