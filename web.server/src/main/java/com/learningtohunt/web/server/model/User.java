package com.learningtohunt.web.server.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
//@FieldsValueMatch(
//        field = "pwd",
//        fieldMatch = "confirmPwd",
//        message = "Passwords do not match!"
//)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    private int userId;

    @NotBlank(message="Email must not be blank")
    @Email(message = "Please provide a valid email address" )
    private String email;

    private boolean emailConfirmed;

    @NotBlank(message="First Name must not be blank")
    @Size(max=100, message="First Name must be no more than 100 characters long")
    private String firstName;

    @NotBlank(message="Last Name must not be blank")
    @Size(max=100, message="Last Name must be no more than 100 characters long")
    private String lastName;

    @NotBlank(message="Password must not be blank")
    @Size(min=6, max = 30, message="Password must be between 7 and 30 characters long")
    //@PasswordValidator
    @JsonIgnore
    private String pwd;

    @Transient
    private String token;

}
