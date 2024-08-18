package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.operations.logout.LogoutInput;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutInputToBlacklistedToken extends AbstractConverter<LogoutInput, BlacklistedToken> {
    private final JwtService jwtService;

    @Override
    protected Class<BlacklistedToken> getTargetClass() {
        return BlacklistedToken.class;
    }

    @Override
    protected BlacklistedToken doConvert(LogoutInput source) {
        BlacklistedToken result = BlacklistedToken.builder()
                .token(source.getToken())
                .expiration(jwtService.getExpiration(source.getToken()))
                .build();

        return result;
    }
}
