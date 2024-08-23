package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.RoleNotFoundException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.promote.Promote;
import com.tinqinacademy.authentication.api.operations.promote.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOutput;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.Role;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.RoleRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class PromoteOperation extends BaseOperation implements Promote {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public PromoteOperation(Validator validator,
                            ConversionService conversionService,
                            ErrorMapper errorMapper,
                            UserRepository userRepository,
                            RoleRepository roleRepository,
                            JwtService jwtService,
                            HttpServletRequest request) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.request = request;
    }

    @Override
    @Transactional
    public Either<ErrorOutput, PromoteOutput> process(PromoteInput input) {
        return Try.of(() -> {
            log.info("Start process method in PromoteOperation. Input: {}", input);

            validate(input);

            User userToPromote = getUserFromInput(input);
            log.info("User for promotion: {}", userToPromote);

            Role adminRole = getAdminRole();

            userToPromote.getRoles().add(adminRole);
            log.info("Added admin role to user");

            userRepository.save(userToPromote);
            log.info("User saved in repository");

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

    private User getUserFromInput(PromoteInput input) {
        return userRepository.findById(UUID.fromString(input.getUserId()))
                .orElseThrow(UserNotFoundException::new);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return (String)request.getAttribute("token");
    }

    private User getUserFromToken(String token) {
        return userRepository.findById(UUID.fromString(jwtService.getUserId(token)))
                .orElseThrow(UserNotFoundException::new);
    }

    private Role getAdminRole() {
        return roleRepository.findByRoleType(RoleType.ADMIN)
                .orElseThrow(RoleNotFoundException::new);
    }
}
