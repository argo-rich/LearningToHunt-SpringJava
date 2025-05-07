package com.learningtohunt.web.server.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateForgottenPassword {

    @NotBlank(message="Email must not be blank")
    @Email(message = "Please provide a valid email address" )
    private String email;

    @NotBlank(message="Reset Token must not be blank")
    @Size(min=35, max=35, message="Reset Token is not correct")
    private String resetToken;

    @NotBlank(message="Password must not be blank")
    @Size(min=7, max = 30, message="Password must be between 7 and 30 characters long")
    private String newPassword;
}
