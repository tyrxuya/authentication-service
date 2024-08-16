package com.tinqinacademy.authentication.core.scheduler;

import com.tinqinacademy.authentication.persistence.entities.BlacklistedToken;
import com.tinqinacademy.authentication.persistence.repositories.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BlacklistedTokenScheduler {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(cron = "0 0-23/4 * * *")
    public void deleteExpiredTokens() {
        Date now = Date.from(Instant.now());

        List<BlacklistedToken> expiredTokens = blacklistedTokenRepository.findAllByExpirationBefore(now);

        blacklistedTokenRepository.deleteAll(expiredTokens);
    }
}
