package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.email.api.operations.recovery.RecoveryInput;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserToRecoveryInput extends AbstractConverter<User, RecoveryInput> {
    @Override
    protected Class<RecoveryInput> getTargetClass() {
        return RecoveryInput.class;
    }

    @Override
    protected RecoveryInput doConvert(User source) {
        RecoveryInput result = RecoveryInput.builder()
                .email(source.getEmail())
                .username(source.getUsername())
                .newPassword(RandomStringUtils.randomAlphanumeric(16))
                .build();

        return result;
    }
}
