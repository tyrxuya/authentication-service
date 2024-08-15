package com.tinqinacademy.authentication.api.operations.validateuser;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ValidateUserInput implements OperationInput {
    @NotBlank
    private String token;

    @NotBlank
    private String username;
}
