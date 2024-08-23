package com.tinqinacademy.authentication.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotAuthenticatedException extends RuntimeException {
    private String message = ExceptionMessages.NOT_AUTHENTICATED;
}
