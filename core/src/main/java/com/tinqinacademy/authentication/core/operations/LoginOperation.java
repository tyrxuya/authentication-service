package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotConfirmedException;
import com.tinqinacademy.authentication.api.operations.login.Login;
import com.tinqinacademy.authentication.api.operations.login.LoginInput;
import com.tinqinacademy.authentication.api.operations.login.LoginOutput;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static io.vavr.API.Match;

@Service
@Slf4j
public class LoginOperation extends BaseOperation implements Login {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginOperation(Validator validator,
                          ConversionService conversionService,
                          ErrorMapper errorMapper,
                          UserRepository userRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<ErrorOutput, LoginOutput> process(LoginInput input) {
        return Try.of(() -> {
            log.info("Start process method in LoginOperation. Input: {}", input);

            validate(input);

            User user = findUserWithUsernameIfExists(input);
            log.info("User found: {}", user);

            checkIfCredentialsMatch(input, user);

            checkIfUserIsConfirmed(user);

            String token = generateJwtToken(user);
            log.info("Token generated: {}", token);

            LoginOutput result = LoginOutput.builder()
                    .token(token)
                    .build();

            log.info("End process method in LoginOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        customCase(throwable, HttpStatus.NOT_FOUND, UsernameNotFoundException.class),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, BadCredentialsException.class),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotConfirmedException.class),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }

    private String generateJwtToken(User user) {
        return jwtService.generateToken(Map.of(
                "userId", user.getId().toString(),
                "username", user.getUsername()
        ));
    }

    private void checkIfUserIsConfirmed(User user) {
        log.info("Start checkIfUserIsConfirmed");
        if (Objects.nonNull(user.getConfirmationCode())) {
            throw new UserNotConfirmedException();
        }
        log.info("End checkIfUserIsConfirmed. User is confirmed");
    }

    private void checkIfCredentialsMatch(LoginInput input, User user) {
        log.info("Start checkIfCredentialsMatch");
        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        log.info("End checkIfCredentialsMatch. Credentials match");
    }

    private User findUserWithUsernameIfExists(LoginInput input) {
        return userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
