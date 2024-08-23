package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.operations.validate.Validate;
import com.tinqinacademy.authentication.api.operations.validate.ValidateInput;
import com.tinqinacademy.authentication.api.operations.validate.ValidateOutput;
import com.tinqinacademy.authentication.core.security.JwtService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class ValidateOperation extends BaseOperation implements Validate {
    private final JwtService jwtService;

    public ValidateOperation(Validator validator,
                             ConversionService conversionService,
                             ErrorMapper errorMapper,
                             JwtService jwtService) {
        super(validator, conversionService, errorMapper);
        this.jwtService = jwtService;
    }

    @Override
    public Either<ErrorOutput, ValidateOutput> process(ValidateInput input) {
        return Try.of(() -> {
            log.info("Start process method in ValidateUserOperation. Input: {}", input);

            validate(input);

            boolean isValid = jwtService.isValid(input.getToken()) && usernamesMatch(input);

            ValidateOutput result = ValidateOutput.builder()
                    .isValid(isValid)
                    .build();

            log.info("End process method in ValidateUserOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }

    private boolean usernamesMatch(ValidateInput input) {
        return jwtService.getUsername(input.getToken()).equals(input.getUsername());
    }
}
