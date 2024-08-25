package com.tinqinacademy.authentication.api.operations.getuser;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetUserOutput implements OperationOutput {
    private String userId;
}
