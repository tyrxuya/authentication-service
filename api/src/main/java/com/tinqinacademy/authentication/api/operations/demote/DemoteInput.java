package com.tinqinacademy.authentication.api.operations.demote;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DemoteInput implements OperationInput {
    @NotBlank
    private String userId;
}
