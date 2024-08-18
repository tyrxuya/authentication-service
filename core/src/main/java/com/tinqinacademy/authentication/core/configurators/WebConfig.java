package com.tinqinacademy.authentication.core.configurators;

import com.tinqinacademy.authentication.core.converters.RegisterInputToUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RegisterInputToUser registerInputToUser;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(registerInputToUser);
    }
}
