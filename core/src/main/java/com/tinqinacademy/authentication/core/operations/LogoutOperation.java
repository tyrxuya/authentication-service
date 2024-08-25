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
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest request;
    private final JwtService jwtService;

    public LogoutOperation(Validator validator,
                           ConversionService conversionService,
                           ErrorMapper errorMapper,
                           BlacklistedTokenRepository blacklistedTokenRepository,
                           HttpServletRequest request,
                           JwtService jwtService) {
        super(validator, conversionService, errorMapper);
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.request = request;
        this.jwtService = jwtService;
    }

    @Override
    public Either<ErrorOutput, LogoutOutput> process(LogoutInput input) {
        return Try.of(() -> {
            log.info("Start process method in LogoutOperation. Input: {}", input);

            validate(input);

            String token = extractTokenFromRequest(request);
            log.info("Extracted token from request: {}", token);

            BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                    .token(token)
                    .expiration(jwtService.getExpiration(token))
                    .build();
            log.info("Token to blacklist: {}", blacklistedToken);

            blacklistedTokenRepository.save(blacklistedToken);
            log.info("Token saved in repository. To be deleted by cron job");

            LogoutOutput result = LogoutOutput.builder().build();

            log.info("End process method in LogoutOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.BAD_REQUEST)
                ));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return (String)request.getAttribute("token");
    }
}
