package com.tinqinacademy.authentication.api.operations.register;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RegisterInput implements OperationInput {
    @NotBlank(message = "username cant be blank")
    private String username;

    @NotBlank(message = "password cant be blank")
    private String password;

    @NotBlank(message = "first name cant be blank")
    private String firstName;

    @NotBlank(message = "last name cant be blank")
    private String lastName;

    @Email(message = "invalid email")
    @NotBlank(message = "email cant be blank")
    private String email;

    @Past(message = "birthday cant be in the future")
    private LocalDate birthday;

    @NotBlank(message = "phone number cant be blank")
    private String phoneNumber;
}
