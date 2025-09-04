package com.f1community.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "app.password-reset")
public class PasswordResetProperties {
    private int expiryMinutes;
    private String redisPrefix;

    // 새로 추가
    private String frontendBaseUrl;
    private String resetPath;
    private String defaultLocale = "en";
    private List<String> allowedLocales = List.of("en", "ko");
    private boolean prefixDefaultLocale = true;

    @Deprecated
    private String frontendResetUrl;
}