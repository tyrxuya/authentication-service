package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.EmailNotFoundException;
import com.tinqinacademy.authentication.api.operations.register.Register;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import com.tinqinacademy.authentication.api.operations.sendconfirmation.SendConfirmation;
import com.tinqinacademy.authentication.api.operations.sendconfirmation.SendConfirmationInput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class RegisterOperation extends BaseOperation implements Register {
    private final UserRepository userRepository;
    private final SendConfirmation sendConfirmation;

    public RegisterOperation(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserRepository userRepository, SendConfirmation sendConfirmation) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.sendConfirmation = sendConfirmation;
    }

    @Override
    public Either<ErrorOutput, RegisterOutput> process(RegisterInput input) {
        return Try.of(() -> {
            log.info("Start process method in RegisterOperation. Input: {}", input);

            validate(input);

            User user = conversionService.convert(input, User.class);

            String confirmationCode = RandomStringUtils.randomAlphanumeric(12);

            user.setConfirmationCode(confirmationCode);

            userRepository.save(user);

            SendConfirmationInput sendConfirmationInput = SendConfirmationInput.builder()
                    .recipient(user.getEmail())
                    .confirmationCode(confirmationCode)
                    .build();

            sendConfirmation.process(sendConfirmationInput);

            RegisterOutput result = RegisterOutput.builder()
                    .id(user.getId().toString())
                    .build();

            log.info("End process method in RegisterOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.BAD_REQUEST),
                        customCase(throwable, HttpStatus.NOT_FOUND, EmailNotFoundException.class),
                        customCase(throwable, HttpStatus.NOT_FOUND, UsernameNotFoundException.class)
                ));
    }
}
