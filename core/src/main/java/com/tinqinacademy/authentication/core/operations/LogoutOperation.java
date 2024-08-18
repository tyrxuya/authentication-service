package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.operations.logout.Logout;
import com.tinqinacademy.authentication.api.operations.logout.LogoutInput;
import com.tinqinacademy.authentication.api.operations.logout.LogoutOutput;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.BlacklistedToken;
import com.tinqinacademy.authentication.persistence.repositories.BlacklistedTokenRepository;
import io.vavr.API;
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
public class LogoutOperation extends BaseOperation implements Logout {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public LogoutOperation(Validator validator,
                           ConversionService conversionService,
                           ErrorMapper errorMapper,
                           BlacklistedTokenRepository blacklistedTokenRepository) {
        super(validator, conversionService, errorMapper);
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    public Either<ErrorOutput, LogoutOutput> process(LogoutInput input) {
        return Try.of(() -> {
            log.info("Start process method in LogoutOperation. Input: {}", input);

            validate(input);

            BlacklistedToken token = conversionService.convert(input, BlacklistedToken.class);

            blacklistedTokenRepository.save(token);

            LogoutOutput result = LogoutOutput.builder().build();

            log.info("End process method in LogoutOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.BAD_REQUEST),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }
}
