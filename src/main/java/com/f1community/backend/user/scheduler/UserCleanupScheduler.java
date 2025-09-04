package com.f1community.backend.user.scheduler;

import com.f1community.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteDeactivatedUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        userRepository.deleteDeactivatedUsersBefore(cutoff);
        log.info("Deleted deactivated users who have been inactive for over 30 days.");
    }
}