package com.tinqinacademy.authentication.api.operations.getuserdetails;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetUserDetailsInput implements OperationInput {
    @NotBlank(message = "username cant be blank")
    private String username;
}
