package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.AuthRestApiPaths;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePassword;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistration;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.api.operations.demote.Demote;
import com.tinqinacademy.authentication.api.operations.demote.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOutput;
import com.tinqinacademy.authentication.api.operations.login.Login;
import com.tinqinacademy.authentication.api.operations.login.LoginInput;
import com.tinqinacademy.authentication.api.operations.login.LoginOutput;
import com.tinqinacademy.authentication.api.operations.logout.Logout;
import com.tinqinacademy.authentication.api.operations.logout.LogoutInput;
import com.tinqinacademy.authentication.api.operations.logout.LogoutOutput;
import com.tinqinacademy.authentication.api.operations.promote.Promote;
import com.tinqinacademy.authentication.api.operations.promote.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOutput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPassword;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.api.operations.register.Register;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import com.tinqinacademy.authentication.api.operations.validate.Validate;
import com.tinqinacademy.authentication.api.operations.validate.ValidateInput;
import com.tinqinacademy.authentication.api.operations.validate.ValidateOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final Login login;
    private final Promote promote;
    private final Demote demote;
    private final ChangePassword changePassword;
    private final RecoverPassword recoverPassword;
    private final Validate validate;
    private final Logout logout;

    @PostMapping(AuthRestApiPaths.REGISTER)
    public ResponseEntity<?> register(@RequestBody RegisterInput input) {
        Either<ErrorOutput, RegisterOutput> result = register.process(input);

        return getOutput(result, HttpStatus.CREATED);
    }

    @PostMapping(AuthRestApiPaths.LOGIN)
    public ResponseEntity<?> login(@RequestBody LoginInput input) {
        Either<ErrorOutput, LoginOutput> result = login.process(input);

        return result.fold(
                errorOutput -> new ResponseEntity<>(errorOutput, HttpStatus.UNAUTHORIZED),
                loginOutput -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Bearer " + loginOutput.getToken());
                    return new ResponseEntity<>(headers, HttpStatus.OK);
                }
        );
    }

    @PostMapping(AuthRestApiPaths.CONFIRM_REGISTRATION)
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationInput input) {
        Either<ErrorOutput, ConfirmRegistrationOutput> result = confirmRegistration.process(input);

        return getOutput(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.PROMOTE)
    public ResponseEntity<?> promote(@RequestBody PromoteInput input) {
        Either<ErrorOutput, PromoteOutput> result = promote.process(input);

        return getOutput(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.DEMOTE)
    public ResponseEntity<?> demote(@RequestBody DemoteInput input) {
        Either<ErrorOutput, DemoteOutput> result = demote.process(input);

        return getOutput(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordInput input) {
        Either<ErrorOutput, ChangePasswordOutput> result = changePassword.process(input);

        return getOutput(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.RECOVER_PASSWORD)
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordInput input) {
        Either<ErrorOutput, RecoverPasswordOutput> result = recoverPassword.process(input);

        return getOutput(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.VALIDATE)
    public ResponseEntity<?> validate(@RequestBody ValidateInput input) {
        Either<ErrorOutput, ValidateOutput> result = validate.process(input);

        return getOutput(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.LOGOUT)
    public ResponseEntity<?> logout(@RequestBody LogoutInput input) {
        Either<ErrorOutput, LogoutOutput> result = logout.process(input);

        return getOutput(result, HttpStatus.OK);
    }
}
