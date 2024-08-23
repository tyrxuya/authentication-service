package com.tinqinacademy.authentication.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidConfirmationCodeException extends RuntimeException {
    private String message = ExceptionMessages.INVALID_CODE;
}
