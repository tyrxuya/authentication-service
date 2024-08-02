package com.tinqinacademy.authentication.api.errors;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Component
public class ErrorOutput {
    private List<Error> errors;
    private HttpStatus status;
}
