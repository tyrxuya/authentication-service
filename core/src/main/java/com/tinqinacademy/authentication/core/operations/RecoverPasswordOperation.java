package com.tinqinacademy.authentication.core.operations;

import com.tinqinacacdemy.email.restexport.EmailClient;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPassword;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import com.tinqinacademy.email.api.operations.recovery.RecoveryInput;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.vavr.API.Match;

@Service
@Slf4j
public class RecoverPasswordOperation extends BaseOperation implements RecoverPassword {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailClient emailClient;

    public RecoverPasswordOperation(Validator validator,
                                    ConversionService conversionService,
                                    ErrorMapper errorMapper,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    EmailClient emailClient) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailClient = emailClient;
    }

    @Override
    public Either<ErrorOutput, RecoverPasswordOutput> process(RecoverPasswordInput input) {
        return Try.of(() -> {
            log.info("Start process method in RecoverPasswordOperation. Input: {}", input);

            validate(input);

            User user = findUserByEmail(input);
            log.info("User found: {}", user);

            RecoveryInput recoveryInput = conversionService.convert(user, RecoveryInput.class);
            log.info("RecoveryInput for email client: {}", recoveryInput);

            user.setPassword(passwordEncoder.encode(recoveryInput.getNewPassword()));
            log.info("New password successfully set");

            userRepository.save(user);
            log.info("User saved in repository");

            emailClient.sendRecovery(recoveryInput);
            log.info("Recovery email sent");

            RecoverPasswordOutput result = RecoverPasswordOutput.builder().build();

            log.info("End process method in RecoverPasswordOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.BAD_REQUEST),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotFoundException.class)
                ));
    }

    private User findUserByEmail(RecoverPasswordInput input) {
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }
}
