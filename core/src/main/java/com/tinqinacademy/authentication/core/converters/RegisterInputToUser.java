package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.exceptions.RoleNotFoundException;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.persistence.entities.Role;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RegisterInputToUser extends AbstractConverter<RegisterInput, User> {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    protected Class<User> getTargetClass() {
        return User.class;
    }

    @Override
    protected User doConvert(RegisterInput source) {
        Set<Role> roles = new HashSet<>();
        roles.add(getUserRole());

        User result = User.builder()
                .username(source.getUsername())
                .password(passwordEncoder.encode(source.getPassword()))
                .email(source.getEmail())
                .birthday(source.getBirthday())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .phoneNumber(source.getPhoneNumber())
                .roles(roles)
                .build();

        return result;
    }

    private Role getUserRole() {
        return roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(RoleNotFoundException::new);
    }
}
