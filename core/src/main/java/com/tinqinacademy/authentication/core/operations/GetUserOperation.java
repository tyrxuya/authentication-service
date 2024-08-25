package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotConfirmedException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.getuser.GetUser;
import com.tinqinacademy.authentication.api.operations.getuser.GetUserInput;
import com.tinqinacademy.authentication.api.operations.getuser.GetUserOutput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
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
public class GetUserOperation extends BaseOperation implements GetUser {
    private final UserRepository userRepository;

    public GetUserOperation(Validator validator,
                            ConversionService conversionService,
                            ErrorMapper errorMapper,
                            UserRepository userRepository) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorOutput, GetUserOutput> process(GetUserInput input) {
        return Try.of(() -> {
            log.info("Start process method in GetUserOperation. Input: {}", input);

            validate(input);

            User user = userRepository.findByUsername(input.getUsername())
                    .orElseThrow(UserNotFoundException::new);

            GetUserOutput result = GetUserOutput.builder()
                    .userId(user.getId().toString())
                    .build();

            log.info("End process method in GetUserOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.BAD_REQUEST),
                        customCase(throwable, HttpStatus.NOT_FOUND, UserNotConfirmedException.class)
                ));
    }
}
