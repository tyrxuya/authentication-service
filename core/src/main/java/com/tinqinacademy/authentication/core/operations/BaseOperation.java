package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.OperationInput;
import com.tinqinacademy.authentication.api.errors.Error;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.InvalidInputException;
import io.vavr.API;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public abstract class BaseOperation {
    protected final Validator validator;
    protected final ConversionService conversionService;
    protected final ErrorMapper errorMapper;

    public <T extends OperationInput> void validate(T input) {
        log.info("Start validating input {} from class {}", input, input.getClass().getSimpleName());

        Set<ConstraintViolation<OperationInput>> violations = validator.validate(input);

        if (!violations.isEmpty()) {
            log.error("Validation not passed! Violations: {}", violations);

            List<Error> errors = new ArrayList<>();

            violations.forEach(violation ->
                    errors.add(Error.builder()
                            .message(violation.getMessage())
                            .field(violation.getPropertyPath().toString())
                            .build())
            );

            throw new InvalidInputException(errors);
        }

        log.info("End validating input. No errors found");
    }

    protected API.Match.Case<? extends Exception, ErrorOutput> customCase(Throwable cause,
                                                                          HttpStatus status,
                                                                          Class<? extends Exception> exceptionClass) {
        return API.Case($(instanceOf(exceptionClass)), errorMapper.map(cause, status));
    }

    protected API.Match.Case<? extends Exception, ErrorOutput> defaultCase(Throwable cause,
                                                                           HttpStatus status) {
        return API.Case($(), errorMapper.map(cause, status));
    }

    protected API.Match.Case<InvalidInputException, ErrorOutput> validateCase(Throwable cause,
                                                                              HttpStatus status) {
        List<Error> errors = cause instanceof InvalidInputException ex ? ex.getErrors() : new ArrayList<>();

        return API.Case($(instanceOf(InvalidInputException.class)), () -> ErrorOutput.builder()
                .errors(errors)
                .status(status)
                .build());
    }

    //sql exception za unique constriant

    //feign exception
}
