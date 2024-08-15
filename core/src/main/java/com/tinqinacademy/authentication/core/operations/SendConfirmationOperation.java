package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.operations.sendconfirmation.SendConfirmation;
import com.tinqinacademy.authentication.api.operations.sendconfirmation.SendConfirmationInput;
import com.tinqinacademy.authentication.api.operations.sendconfirmation.SendConfirmationOutput;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import static io.vavr.API.Match;

@Service
@Slf4j
public class SendConfirmationOperation extends BaseOperation implements SendConfirmation {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${sender.email}")
    private String sender;

    public SendConfirmationOperation(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, JavaMailSender mailSender, TemplateEngine templateEngine) {
        super(validator, conversionService, errorMapper);
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public Either<ErrorOutput, SendConfirmationOutput> process(SendConfirmationInput input) {
        return Try.of(() -> {
            Context context = new Context(Locale.ENGLISH, Map.of("email", input.getRecipient(),
                    "confirmationCode", input.getConfirmationCode())
            );

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            mimeMessageHelper.setTo(input.getRecipient());
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setSubject("Registration Confirmation");
            mimeMessageHelper.setText(templateEngine.process("confirmation", context), true);
            mailSender.send(mimeMessage);

            SendConfirmationOutput result = SendConfirmationOutput.builder().build();

            return result;
        })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        defaultCase(throwable, HttpStatus.I_AM_A_TEAPOT)
                ));
    }
}
