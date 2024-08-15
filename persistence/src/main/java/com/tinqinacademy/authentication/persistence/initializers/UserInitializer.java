package com.tinqinacademy.authentication.persistence.initializers;

import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User admin = User.builder()
                .username("admin")
                .password("admin")
                .role(RoleType.ADMIN)
                .email("hotel@tinqin.com")
                .firstName("")
                .lastName("")
                .birthday(LocalDate.now())
                .phoneNumber("0888888888")
                .build();

        userRepository.findUsersByRole(RoleType.ADMIN)
                .stream()
                .filter(user -> user.getRole().equals(RoleType.ADMIN))
                .findFirst()
                .ifPresentOrElse(
                        user -> {},
                        () -> userRepository.save(admin)
                );
    }
}
