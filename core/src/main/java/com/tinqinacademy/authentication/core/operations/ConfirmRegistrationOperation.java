package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.EmailNotFoundException;
import com.tinqinacademy.authentication.api.exceptions.InvalidConfirmationCodeException;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistration;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.persistence.entities.Role;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

import static io.vavr.API.Match;

@Service
@Slf4j
public class ConfirmRegistrationOperation extends BaseOperation implements ConfirmRegistration {
    private final UserRepository userRepository;

    public ConfirmRegistrationOperation(Validator validator,
                                        ConversionService conversionService,
                                        ErrorMapper errorMapper,
                                        UserRepository userRepository) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorOutput, ConfirmRegistrationOutput> process(ConfirmRegistrationInput input) {
        return Try.of(() -> {
            log.info("Start process method in ConfirmRegistrationOperation. Input: {}", input);

            validate(input);

            User user = findUserWithConfirmationCode(input);
            log.info("Found user matching the confirmation code: {}", user);

            user.setConfirmationCode(null);
            log.info("User confirmed successfully");

            userRepository.save(user);
            log.info("User saved in repository");

            ConfirmRegistrationOutput result = ConfirmRegistrationOutput.builder().build();

            log.info("End process method in ConfirmRegistrationOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        customCase(throwable, HttpStatus.BAD_REQUEST, InvalidConfirmationCodeException.class),
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }

    private User findUserWithConfirmationCode(ConfirmRegistrationInput input) {
        return userRepository.searchDistinctByConfirmationCode(input.getConfirmationCode())
                .orElseThrow(InvalidConfirmationCodeException::new);
    }
}
