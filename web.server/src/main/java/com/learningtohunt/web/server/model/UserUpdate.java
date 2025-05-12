package com.learningtohunt.web.server.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdate {

    @Min(value=1, message="User Id must be greater than 0")
    private int userId;

    @NotBlank(message="Email must not be blank")
    @Email(message = "Please provide a valid email address" )
    private String email;

    @NotBlank(message="First Name must not be blank")
    @Size(max=100, message="First Name must be no more than 100 characters long")
    private String firstName;

    @NotBlank(message="Last Name must not be blank")
    @Size(max=100, message="Last Name must be no more than 100 characters long")
    private String lastName;

    private String currentPassword;

    private String password;
    private String confirmPassword;
}
