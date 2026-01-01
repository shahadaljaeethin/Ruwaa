package org.example.ruwaa.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCustomerRequest
{
    @Email
    @NotEmpty(message = "email is required")
    private String email;
    @NotEmpty(message = "name is required")
    private String name;
    @NotEmpty(message = "username is required")
    private String username;
    @Size(min = 8, max = 20)
    @NotEmpty(message = "password is required")
    @Pattern(
            regexp = "^[A-Za-z0-9]+$",
            message = "Password must be at least 8 characters long and contain letters and numbers"
    )
    @NotEmpty(message = "password is required")
    private String password;
    @NotEmpty(message = "enter phone number")
    @Size(min = 10,max = 10,message = "invalid phone number")
    @Pattern(regexp = "^(?:\\+966|966|0)?5\\d{8}$",message = "phone form must be like 05*******")
    private String phone_number;
}
