package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPassword;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import static io.vavr.API.Match;

@Service
@Slf4j
public class RecoverPasswordOperation extends BaseOperation implements RecoverPassword {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sender.email}")
    private String sender;

    public RecoverPasswordOperation(Validator validator,
                                    ConversionService conversionService,
                                    ErrorMapper errorMapper,
                                    JavaMailSender mailSender,
                                    TemplateEngine templateEngine,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        super(validator, conversionService, errorMapper);
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<ErrorOutput, RecoverPasswordOutput> process(RecoverPasswordInput input) {
        return Try.of(() -> {
            log.info("Start process method in RecoverPasswordOperation. Input: {}", input);

            validate(input);

            User user = findUserByEmail(input);

            String newPassword = RandomStringUtils.randomAlphanumeric(16);

            user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);

            Context context = new Context(Locale.ENGLISH, Map.of("username", user.getUsername(),
                    "password", newPassword)
            );

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            setupMimeMessageHelper(user, mimeMessageHelper, context);

            mailSender.send(mimeMessage);

            RecoverPasswordOutput result = RecoverPasswordOutput.builder().build();

            log.info("End process method in RecoverPasswordOperation. Result: {}", result);

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        validateCase(throwable, HttpStatus.I_AM_A_TEAPOT),
                        customCase(throwable, HttpStatus.UNAUTHORIZED, UserNotFoundException.class),
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }

    private void setupMimeMessageHelper(User user, MimeMessageHelper mimeMessageHelper, Context context) throws MessagingException {
        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setFrom(sender);
        mimeMessageHelper.setSubject("Requested password change");
        mimeMessageHelper.setText(templateEngine.process("recovery", context), true);
    }

    private User findUserByEmail(RecoverPasswordInput input) {
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }
}
