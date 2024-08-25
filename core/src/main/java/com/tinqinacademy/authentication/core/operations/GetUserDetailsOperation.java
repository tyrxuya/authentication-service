package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotConfirmedException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.getuserdetails.GetUserDetails;
import com.tinqinacademy.authentication.api.operations.getuserdetails.GetUserDetailsInput;
import com.tinqinacademy.authentication.api.operations.getuserdetails.GetUserDetailsOutput;
import com.tinqinacademy.authentication.core.security.CustomUserDetailsService;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static io.vavr.API.Match;

@Service
@Slf4j
public class GetUserDetailsOperation extends BaseOperation implements GetUserDetails {
    private final CustomUserDetailsService userDetailsService;

    public GetUserDetailsOperation(Validator validator,
                                   ConversionService conversionService,
                                   ErrorMapper errorMapper,
                                   CustomUserDetailsService userDetailsService) {
        super(validator, conversionService, errorMapper);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Either<ErrorOutput, GetUserDetailsOutput> process(GetUserDetailsInput input) {
        return Try.of(() -> {
            log.info("Start process method in GetUserDetailsOperation. Input: {}", input);

            validate(input);

            User user = (User) userDetailsService.loadUserByUsername(input.getUsername());

            Set<String> authorities = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            GetUserDetailsOutput result = GetUserDetailsOutput.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();

            log.info("End process method in GetUserDetailsOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.BAD_REQUEST),
                        customCase(throwable, HttpStatus.NOT_FOUND, UserNotFoundException.class),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotConfirmedException.class)
                ));
    }
}
