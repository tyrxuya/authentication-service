package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePassword;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class ChangePasswordOperation extends BaseOperation implements ChangePassword {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordOperation(Validator validator,
                                   ConversionService conversionService,
                                   ErrorMapper errorMapper,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<ErrorOutput, ChangePasswordOutput> process(ChangePasswordInput input) {
        return Try.of(() -> {
            log.info("Start process method in ChangePasswordOperation. Input: {}", input);

            validate(input);

            User user = findUsernameByEmail(input);

            checkIfOldPasswordMatchesUser(input, user);

            user.setPassword(passwordEncoder.encode(input.getNewPassword()));

            userRepository.save(user);

            ChangePasswordOutput result = ChangePasswordOutput.builder().build();

            log.info("End process method in ChangePasswordOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotFoundException.class),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, BadCredentialsException.class),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }

    private void checkIfOldPasswordMatchesUser(ChangePasswordInput input, User user) {
        if (!passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password does not match");
        }
    }

    private User findUsernameByEmail(ChangePasswordInput input) {
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }
}
