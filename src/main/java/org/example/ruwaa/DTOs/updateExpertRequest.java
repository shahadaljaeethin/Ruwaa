package org.example.ruwaa.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class updateExpertRequest
{
    private String username;
    private String about_me;
    private String linkedin_url;
    private String email;
    private String phone_number;
}
