package com.tinqinacademy.authentication.core.configurators;

import com.tinqinacademy.authentication.core.converters.LogoutInputToBlacklistedToken;
import com.tinqinacademy.authentication.core.converters.RegisterInputToUser;
import com.tinqinacademy.authentication.core.converters.UserToConfirmationInput;
import com.tinqinacademy.authentication.core.converters.UserToRecoveryInput;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RegisterInputToUser registerInputToUser;
    private final UserToConfirmationInput userToConfirmationInput;
    private final UserToRecoveryInput userToRecoveryInput;
    private final LogoutInputToBlacklistedToken logoutInputToBlacklistedToken;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(registerInputToUser);
        registry.addConverter(userToConfirmationInput);
        registry.addConverter(userToRecoveryInput);
        registry.addConverter(logoutInputToBlacklistedToken);
    }
}
