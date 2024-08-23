package com.tinqinacademy.authentication.core.converters;

import com.tinqinacademy.authentication.api.operations.logout.LogoutInput;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.BlacklistedToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutInputToBlacklistedToken extends AbstractConverter<LogoutInput, BlacklistedToken> {
    private final JwtService jwtService;
    private final HttpServletRequest request;

    @Override
    protected Class<BlacklistedToken> getTargetClass() {
        return BlacklistedToken.class;
    }

    @Override
    protected BlacklistedToken doConvert(LogoutInput source) {
        String token = (String)request.getAttribute("token");

        BlacklistedToken result = BlacklistedToken.builder()
                .token(token)
                .expiration(jwtService.getExpiration(token))
                .build();

        return result;
    }
}
