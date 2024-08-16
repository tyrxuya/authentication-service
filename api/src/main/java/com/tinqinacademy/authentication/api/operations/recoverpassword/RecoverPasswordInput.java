package com.tinqinacademy.authentication.api.operations.recoverpassword;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RecoverPasswordInput implements OperationInput {
    @Email(message = "invalid email")
    @NotBlank(message = "email cant be blank")
    private String email;
}
