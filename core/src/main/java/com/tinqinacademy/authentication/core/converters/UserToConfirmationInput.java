package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.email.api.operations.confirmation.ConfirmationInput;
import org.springframework.stereotype.Component;

@Component
public class UserToConfirmationInput extends AbstractConverter<User, ConfirmationInput> {
    @Override
    protected Class<ConfirmationInput> getTargetClass() {
        return ConfirmationInput.class;
    }

    @Override
    protected ConfirmationInput doConvert(User source) {
        ConfirmationInput result = ConfirmationInput.builder()
                .confirmationCode(source.getConfirmationCode())
                .recipient(source.getEmail())
                .username(source.getUsername())
                .build();

        return result;
    }
}
