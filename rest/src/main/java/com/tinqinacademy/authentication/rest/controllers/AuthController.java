package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.paths.AuthRestApiPaths;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import jakarta.servlet.http.HttpServletRequest;
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
    @Operation(
            summary = "Register REST API",
            description = "User receives a confirmation email once registered. User cannot login until email is confirmed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<?> register(@RequestBody RegisterInput input) {
        Either<ErrorOutput, RegisterOutput> result = register.process(input);

        return handleOperationResult(result, HttpStatus.CREATED);
    }

    @PostMapping(AuthRestApiPaths.LOGIN)
    @Operation(
            summary = "Login REST API",
            description = "Logins the user and issues a JWT with 5 min validity."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Wrong credentials")
    })
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
    @Operation(
            summary = "Confirm registration REST API",
            description = "Activates user account and allows for login to complete successfully."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationInput input) {
        Either<ErrorOutput, ConfirmRegistrationOutput> result = confirmRegistration.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.PROMOTE)
    @Operation(
            summary = "Promote REST API",
            description = "Promotes user to admin and give admin rights. Only an admin can promote another user to admin."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promote successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> promote(@RequestBody PromoteInput input) {
        Either<ErrorOutput, PromoteOutput> result = promote.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.DEMOTE)
    @Operation(
            summary = "Demote REST API",
            description = "Removes admin privileges of any user. System must always have at least 1 admin. Admin cannot demote themselves."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demote successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> demote(@RequestBody DemoteInput input, HttpServletRequest request) {
        input.setLoggedUserId((String)request.getAttribute("userId"));

        Either<ErrorOutput, DemoteOutput> result = demote.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.CHANGE_PASSWORD)
    @Operation(
            summary = "Change password REST API",
            description = "Changes the user password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changes successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordInput input) {
        Either<ErrorOutput, ChangePasswordOutput> result = changePassword.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.RECOVER_PASSWORD)
    @Operation(
            summary = "Recover password REST API",
            description = "Send email with password recovery code only if the email is registered to a user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password recovery successful")
    })
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordInput input) {
        Either<ErrorOutput, RecoverPasswordOutput> result = recoverPassword.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.VALIDATE)
    @Operation(
            summary = "Validate REST API",
            description = "Validates the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token verified"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<?> validate(@RequestBody ValidateInput input) {
        Either<ErrorOutput, ValidateOutput> result = validate.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }

    @PostMapping(AuthRestApiPaths.LOGOUT)
    @Operation(
            summary = "Logout REST API",
            description = "Logs the user out of the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> logout(@RequestBody LogoutInput input) {
        Either<ErrorOutput, LogoutOutput> result = logout.process(input);

        return handleOperationResult(result, HttpStatus.OK);
    }
}
