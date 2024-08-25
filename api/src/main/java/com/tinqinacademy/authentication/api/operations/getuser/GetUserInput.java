package com.tinqinacademy.authentication.api.operations.getuser;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetUserInput implements OperationInput {
    @NotBlank(message = "username cant be blank")
    private String username;
}
