package com.tinqinacademy.authentication.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailNotFoundException extends RuntimeException {
    private String message = ExceptionMessages.EMAIL_NOT_FOUND;
}
