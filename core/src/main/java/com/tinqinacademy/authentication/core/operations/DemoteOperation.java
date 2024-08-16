package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.InvalidDemoteException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.demote.Demote;
import com.tinqinacademy.authentication.api.operations.demote.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOutput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.API;
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
public class DemoteOperation extends BaseOperation implements Demote {
    private final UserRepository userRepository;

    public DemoteOperation(Validator validator,
                           ConversionService conversionService,
                           ErrorMapper errorMapper,
                           UserRepository userRepository) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<ErrorOutput, DemoteOutput> process(DemoteInput input) {
        return Try.of(() -> {
            log.info("Start process method in DemoteOperation. Input: {}", input);

            validate(input);

            User user = userRepository.findById(UUID.fromString(input.getUserId()))
                    .orElseThrow(UserNotFoundException::new);

            Integer adminCount = userRepository.findUsersByRole(RoleType.ADMIN).size();

            if (adminCount <= 1) {
                throw new InvalidDemoteException();
            }

            user.setRole(RoleType.USER);

            userRepository.save(user);

            DemoteOutput result = DemoteOutput.builder().build();

            log.info("End process method in DemoteOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotFoundException.class),
                        customCase(throwable, HttpStatus.FORBIDDEN, InvalidDemoteException.class),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }
}
