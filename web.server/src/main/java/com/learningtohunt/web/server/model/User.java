package com.learningtohunt.web.server.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
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

    @JsonIgnore
    private String pwd;

    @Transient
    private String token;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public List<String> getRoleNames() { return roles.stream().map(Role::getRoleName).toList(); }

}
