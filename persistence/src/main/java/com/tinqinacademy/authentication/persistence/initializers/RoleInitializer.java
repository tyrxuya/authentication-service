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
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class RoleInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("role initializer called");
        Role userRole = Role.builder()
                .roleType(RoleType.USER)
                .build();

        Role adminRole = Role.builder()
                .roleType(RoleType.ADMIN)
                .build();

        if (roleRepository.findByRoleType(RoleType.USER).isEmpty()) {
            roleRepository.save(userRole);
        }

        if (roleRepository.findByRoleType(RoleType.ADMIN).isEmpty()) {
            roleRepository.save(adminRole);
        }

//        userRole = roleRepository.findByRoleType(RoleType.USER).get();
//        adminRole = roleRepository.findByRoleType(RoleType.ADMIN).get();

//        User admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles(Set.of(userRole, adminRole))
//                .email("hotel@tinqin.com")
//                .firstName("")
//                .lastName("")
//                .birthday(LocalDate.now())
//                .phoneNumber("0888888888")
//                .build();
//
//        userRepository.findUsersByRoles_RoleType(RoleType.ADMIN)
//                .stream()
//                .filter(user -> user.getRoles()
//                        .stream()
//                        .anyMatch(role -> role.getRoleType().equals(RoleType.ADMIN)))
//                .findFirst()
//                .ifPresentOrElse(
//                        user -> {},
//                        () -> userRepository.save(admin)
//                );
    }
}
