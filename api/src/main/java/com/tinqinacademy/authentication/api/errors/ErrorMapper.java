package com.tinqinacademy.authentication.api.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Component
public class ErrorMapper {
    private final ErrorOutput errorOutput;

    public <T extends Throwable> ErrorOutput map(T throwable, HttpStatus httpStatus) {
        errorOutput.setErrors(List.of(Error.builder()
                .message(throwable.getMessage())
                .build()));

        errorOutput.setStatus(httpStatus);

        return errorOutput;
    }
}
