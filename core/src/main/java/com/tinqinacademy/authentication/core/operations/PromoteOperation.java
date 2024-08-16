package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.promote.Promote;
import com.tinqinacademy.authentication.api.operations.promote.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOutput;
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

import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class PromoteOperation extends BaseOperation implements Promote {
    private final UserRepository userRepository;

    public PromoteOperation(Validator validator,
                            ConversionService conversionService,
                            ErrorMapper errorMapper,
                            UserRepository userRepository) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorOutput, PromoteOutput> process(PromoteInput input) {
        return Try.of(() -> {
            log.info("Start process method in PromoteOperation. Input: {}", input);

            validate(input);

            User user = findUserById(input);

            user.setRole(RoleType.ADMIN);

            userRepository.save(user);

            PromoteOutput result = PromoteOutput.builder().build();

            log.info("End process method in PromoteOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotFoundException.class),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }

    private User findUserById(PromoteInput input) {
        return userRepository.findById(UUID.fromString(input.getUserId()))
                .orElseThrow(UserNotFoundException::new);
    }
}
