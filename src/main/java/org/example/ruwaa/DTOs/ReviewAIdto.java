package org.example.ruwaa.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewAIdto {

    private String rate;
    private String criteria;
    private String comment;
    private String suggestion;

    }
