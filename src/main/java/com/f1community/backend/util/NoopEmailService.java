package com.f1community.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"dev","test"})
@Service
@Slf4j
public class NoopEmailService implements EmailService {

    @Override
    public void sendPasswordResetEmail(String to, String subject, String resetLink) {
        logResetEmail(to, subject, resetLink, null);
    }

    @Override
    public void sendPasswordResetEmail(String to, String subject, String resetLink, String fullName) {
        logResetEmail(to, subject, resetLink, fullName);
    }

    private void logResetEmail(String to, String subject, String resetLink, String fullName) {
        String displayName = (fullName != null && !fullName.isBlank()) ? fullName : "안녕하세요";
        String userInfo = (fullName != null)
                ? ", user=" + displayName
                : "";
        String link = "DEV/TEST 링크 미리보기: (subject=" + subject + userInfo + ") ?token=" + resetLink;
        log.info("[NOOP] Password reset -> {} : {}", to, link);
    }
}