package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.ErrorOutput;
import com.tinqinacademy.authentication.api.exceptions.InvalidDemoteException;
import com.tinqinacademy.authentication.api.exceptions.RoleNotFoundException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
import com.tinqinacademy.authentication.api.operations.demote.Demote;
import com.tinqinacademy.authentication.api.operations.demote.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOutput;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class DemoteOperation extends BaseOperation implements Demote {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public DemoteOperation(Validator validator,
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
    public Either<ErrorOutput, DemoteOutput> process(DemoteInput input) {
        return Try.of(() -> {
            log.info("Start process method in DemoteOperation. Input: {}", input);

            validate(input);

            String token = extractTokenFromRequest(request);

            User loggedUser = findUserById(jwtService.getUserId(token));
            log.info("loggedUser: {}", loggedUser);

            User userToDemote = findUserById(input.getUserId());
            log.info("userToDemote: {}", userToDemote);

            checkIfDemoteIsPossible();

            checkIfUserDemotesThemselves(loggedUser, userToDemote);

            Role adminRole = getAdminRole();

            userToDemote.getRoles().remove(adminRole);
            log.info("Removed admin role from user. User: {}", userToDemote);

            userRepository.save(userToDemote);
            log.info("User saved in repository");

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

    private String extractTokenFromRequest(HttpServletRequest request) {
        return (String)request.getAttribute("token");
    }

    private void checkIfUserDemotesThemselves(User loggedUser, User userToDemote) {
        log.info("Start checkIfUserDemotesThemselves");
        if (loggedUser.getId().equals(userToDemote.getId())) {
            throw new InvalidDemoteException();
        }
        log.info("End checkIfUserDemotesThemselves. User is NOT demoting themselves");
    }

    private void checkIfDemoteIsPossible() {
        log.info("Start checkIfDemoteIsPossible");
        if (getAdminCount() <= 1) {
            throw new InvalidDemoteException();
        }
        log.info("End checkIfDemoteIsPossible. Demote is possible");
    }

    private Long getAdminCount() {
        return userRepository.countUserByRolesContaining(getAdminRole());
    }

    private User findUserById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(UserNotFoundException::new);
    }

    private Role getAdminRole() {
        return roleRepository.findByRoleType(RoleType.ADMIN)
                .orElseThrow(RoleNotFoundException::new);
    }
}
