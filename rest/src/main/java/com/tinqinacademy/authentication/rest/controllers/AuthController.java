package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistration;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.api.operations.register.Register;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication REST APIs")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final Register register;
    private final ConfirmRegistration confirmRegistration;

    @PostMapping("api/v1/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterInput input) {
        Either<ErrorOutput, RegisterOutput> result = register.process(input);

        return getOutput(result, HttpStatus.CREATED);
    }

    @PostMapping("api/v1/auth/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationInput input) {
        Either<ErrorOutput, ConfirmRegistrationOutput> result = confirmRegistration.process(input);

        return getOutput(result, HttpStatus.OK);
    }
}
