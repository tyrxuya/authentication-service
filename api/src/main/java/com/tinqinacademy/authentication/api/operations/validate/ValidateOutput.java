package com.tinqinacademy.authentication.api.operations.validate;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ValidateOutput implements OperationOutput {
    private boolean isValid;
}
