package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterInputToUser extends AbstractConverter<RegisterInput, User> {
    private final PasswordEncoder passwordEncoder;

    @Override
    protected Class<User> getTargetClass() {
        return User.class;
    }

    @Override
    protected User doConvert(RegisterInput source) {
        User result = User.builder()
                .username(source.getUsername())
                .password(passwordEncoder.encode(source.getPassword()))
                .email(source.getEmail())
                .birthday(source.getBirthday())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .phoneNumber(source.getPhoneNumber())
                .role(RoleType.USER)
                .build();

        return result;
    }
}
