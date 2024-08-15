package com.tinqinacademy.authentication.api.operations.changepassword;

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
public class ChangePasswordInput implements OperationInput {
    @NotBlank()
    private String oldPassword;

    @NotBlank()
    private String newPassword;

    @Email
    @NotBlank
    private String email;
}
