package com.tinqinacademy.authentication.rest.configurations;

import com.tinqinacademy.authentication.api.paths.AuthRestApiPaths;
import com.tinqinacademy.authentication.core.converters.LogoutInputToBlacklistedToken;
import com.tinqinacademy.authentication.core.converters.RegisterInputToUser;
import com.tinqinacademy.authentication.core.converters.UserToConfirmationInput;
import com.tinqinacademy.authentication.core.converters.UserToRecoveryInput;
import com.tinqinacademy.authentication.rest.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    private final RegisterInputToUser registerInputToUser;
    private final UserToConfirmationInput userToConfirmationInput;
    private final UserToRecoveryInput userToRecoveryInput;
    private final LogoutInputToBlacklistedToken logoutInputToBlacklistedToken;

    private final AuthInterceptor authInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(registerInputToUser);
        registry.addConverter(userToConfirmationInput);
        registry.addConverter(userToRecoveryInput);
        registry.addConverter(logoutInputToBlacklistedToken);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("adding interceptor");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(AuthRestApiPaths.DEMOTE)
                .addPathPatterns(AuthRestApiPaths.PROMOTE)
                .addPathPatterns(AuthRestApiPaths.LOGOUT)
                .addPathPatterns(AuthRestApiPaths.CHANGE_PASSWORD);
    }
}
