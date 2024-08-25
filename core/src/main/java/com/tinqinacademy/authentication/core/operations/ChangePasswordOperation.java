package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.Error;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.InvalidInputException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePassword;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class ChangePasswordOperation extends BaseOperation implements ChangePassword {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public ChangePasswordOperation(Validator validator,
                                   ConversionService conversionService,
                                   ErrorMapper errorMapper,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService,
                                   HttpServletRequest request) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.request = request;
    }

    @Override
    public Either<ErrorOutput, ChangePasswordOutput> process(ChangePasswordInput input) {
        return Try.of(() -> {
            log.info("Start process method in ChangePasswordOperation. Input: {}", input);

            validate(input);

            String token = extractTokenFromRequest(request);
            log.info("Extracted token from request: {}", token);

            User user = getUserFromToken(token);
            log.info("Logged user: {}", user);

            checkIfOldPasswordMatchesUser(input, user);

            checkIfEmailMatchesUser(input, user);

            checkIfNewPasswordMatchesCurrentPassword(input, user);

            user.setPassword(passwordEncoder.encode(input.getNewPassword()));
            log.info("New password set successfully");

            userRepository.save(user);
            log.info("User saved in repository");

            ChangePasswordOutput result = ChangePasswordOutput.builder().build();

            log.info("End process method in ChangePasswordOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotFoundException.class),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, BadCredentialsException.class)
                ));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return (String)request.getAttribute("token");
    }

    private void checkIfNewPasswordMatchesCurrentPassword(ChangePasswordInput input, User user) {
        log.info("Start checkIfNewPasswordMatchesCurrentPassword");
        if (passwordEncoder.matches(input.getNewPassword(), user.getPassword())) {
            List<Error> errors = List.of(Error.builder()
                    .message("New password the same as the old one")
                    .field("newPassword")
                    .build());

            throw new InvalidInputException(errors);
        }
        log.info("End checkIfNewPasswordMatchesCurrentPassword. Passwords are different");
    }

    private void checkIfEmailMatchesUser(ChangePasswordInput input, User user) {
        log.info("Start checkIfEmailMatchesUser");
        if (!user.getEmail().equals(input.getEmail())) {
            throw new BadCredentialsException("Emails dont match");
        }
        log.info("End checkIfEmailMatchesUser. Email match found");
    }

    private User getUserFromToken(String token) {
        return userRepository.findById(UUID.fromString(jwtService.getUserId(token)))
                .orElseThrow(UserNotFoundException::new);
    }

    private void checkIfOldPasswordMatchesUser(ChangePasswordInput input, User user) {
        log.info("Start checkIfOldPasswordMatchesUser");
        if (!passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password does not match");
        }
        log.info("End checkIfOldPasswordMatchesUser. Old password match found");
    }
}
