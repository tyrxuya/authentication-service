package com.tinqinacademy.authentication.api.operations.validateuser;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ValidateUserOutput implements OperationOutput {
    private boolean isValid;
}
