package com.tinqinacademy.authentication.api.operations.confirmregistration;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ConfirmRegistrationInput implements OperationInput {
    @NotBlank(message = "confirmation code cant be blank")
    private String confirmationCode;
}
