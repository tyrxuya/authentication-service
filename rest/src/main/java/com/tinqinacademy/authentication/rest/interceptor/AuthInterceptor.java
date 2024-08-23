package com.tinqinacademy.authentication.rest.interceptor;

import com.tinqinacademy.authentication.api.paths.AuthRestApiPaths;
import com.tinqinacademy.authentication.core.security.JwtService;
import com.tinqinacademy.authentication.persistence.entities.User;
import com.tinqinacademy.authentication.persistence.enums.RoleType;
import com.tinqinacademy.authentication.persistence.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final List<String> ADMIN_PATHS = List.of(
            AuthRestApiPaths.PROMOTE,
            AuthRestApiPaths.DEMOTE
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("intercepted");
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("not authorized");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String token = authorization.substring(7);
        if (!jwtService.isValid(token)) {
            log.info("token not valid");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String userId = jwtService.getUserId(token);
        Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));
        if (userOptional.isEmpty()) {
            log.info("user not found");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        User user = userOptional.get();
        if (ADMIN_PATHS.contains(request.getRequestURI()) && userIsNotAdmin(user)) {
            log.info("user is not admin");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("token", token);
        return true;
    }

    private boolean userIsNotAdmin(User user) {
        return user.getRoles()
                .stream()
                .noneMatch(role -> role.getRoleType().equals(RoleType.ADMIN));
    }
}
