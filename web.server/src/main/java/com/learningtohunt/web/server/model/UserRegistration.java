package com.learningtohunt.web.server.model;

import com.learningtohunt.web.server.annotation.FieldsValueMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data needed to register a new user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldsValueMatch(
        field = "password",
        fieldMatch = "confirmPassword",
        message = "Passwords do not match!"
)
public class UserRegistration {

    @NotBlank(message="Email must not be blank")
    @Email(message = "Please provide a valid email address" )
    private String email;

    @NotBlank(message="Password must not be blank")
    @Size(min=7, max = 30, message="Password must be between 7 and 30 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+{7,30}$", message = "Password must contain at least an uppercase letter, a lowercase letter, a number and a special character")
    private String password;

    @NotBlank(message="Confirm Password must not be blank")
    @Size(min=7, max = 30, message="ConfirmPassword must be between 7 and 30 characters long")
    private String confirmPassword;

    @NotBlank(message="First Name must not be blank")
    @Size(max=100, message="First Name must be no more than 100 characters long")
    private String firstName;

    @NotBlank(message="Last Name must not be blank")
    @Size(max=100, message="Last Name must be no more than 100 characters long")
    private String lastName;
}
