package org.example.ruwaa.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDTO {

    @NotEmpty(message = "enter review")
    @Size(min=60,message = "minimum review length is 60character")
    private String content;
}
