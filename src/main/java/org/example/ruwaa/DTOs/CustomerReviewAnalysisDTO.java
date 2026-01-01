package org.example.ruwaa.DTOs;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerReviewAnalysisDTO {
    private List<String> skills;
    private List<String> strengths;
    private List<String> weaknesses;
    private Integer overallRating;

}
