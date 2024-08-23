package com.tinqinacademy.authentication.persistence.initializers;

import com.tinqinacademy.authentication.persistence.entities.Role;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.RoleRepository;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(2)
@Slf4j
public class UserInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("user initializer called");

        Role userRole = roleRepository.findByRoleType(RoleType.USER).get();
        Role adminRole = roleRepository.findByRoleType(RoleType.ADMIN).get();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(roles)
                .email("hotel@tinqin.com")
                .firstName("")
                .lastName("")
                .birthday(LocalDate.now())
                .phoneNumber("0888888888")
                .build();

        userRepository.findUsersByRoles_RoleType(RoleType.ADMIN)
                .stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> role.getRoleType().equals(RoleType.ADMIN)))
                .findFirst()
                .ifPresentOrElse(
                        user -> {},
                        () -> userRepository.save(admin)
                );
    }
}
