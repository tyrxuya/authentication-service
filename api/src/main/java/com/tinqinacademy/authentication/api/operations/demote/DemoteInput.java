package com.tinqinacademy.authentication.api.operations.demote;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String loggedUserId;

    @NotBlank
    private String userId;
}
