package com.tinqinacademy.authentication.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidDemoteException extends RuntimeException {
    private String message = ExceptionMessages.DEMOTE_NOT_POSSIBLE;
}
