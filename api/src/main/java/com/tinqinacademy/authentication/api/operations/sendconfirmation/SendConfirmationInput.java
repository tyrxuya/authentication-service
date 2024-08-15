package com.tinqinacademy.authentication.api.operations.sendconfirmation;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendConfirmationInput implements OperationInput {
    private String recipient;
    private String confirmationCode;
}
