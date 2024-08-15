package com.tinqinacademy.authentication.api.operations.promote;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PromoteInput implements OperationInput {
    @NotBlank
    private String userId;
}
