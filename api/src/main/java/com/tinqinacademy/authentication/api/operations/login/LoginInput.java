package com.tinqinacademy.authentication.api.operations.login;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class LoginInput implements OperationInput {
    @NotBlank(message = "username cant be blank")
    private String username;

    @NotBlank(message = "password cant be blank")
    private String password;
}
