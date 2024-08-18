package com.tinqinacademy.authentication.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacacdemy.email.restexport.EmailClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RestExportConfig {
    private final ApplicationContext applicationContext;

    @Value("${email.client.url}")
    private String EMAIL_URL;
    @Bean
    public EmailClient getEmailClient() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return Feign.builder()
                .encoder(new JacksonEncoder(applicationContext.getBean(ObjectMapper.class)))
                .decoder(new JacksonDecoder(applicationContext.getBean(ObjectMapper.class)))
                .target(EmailClient.class, EMAIL_URL);
    }
}
