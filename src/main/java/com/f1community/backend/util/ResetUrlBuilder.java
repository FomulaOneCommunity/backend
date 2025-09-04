package com.f1community.backend.util;

import com.f1community.backend.config.PasswordResetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResetUrlBuilder {

    private final PasswordResetProperties props;

    /**
     * 비밀번호 재설정 링크 생성 (NPE 안전 / 속성 검증 / 로케일 정규화)
     */
    public String build(String token, String requestedLocale, String userPreferredLocale) {
        // 토큰은 필수
        String safeToken = requireNonBlank(token, "token");

        // 필수 속성 검증
        String base = stripTrailing(requireNonBlank(props.getFrontendBaseUrl(), "frontendBaseUrl"), '/');
        String path = stripLeading(requireNonBlank(props.getResetPath(), "resetPath"), '/');

        // 로케일 선택 (항상 non-null, 정규화됨)
        String locale = pickLocale(props, requestedLocale, userPreferredLocale);

        // 기본 로케일과 비교 시 NPE가 발생하지 않도록 미리 정규화해서 비교
        String defaultLocale = normalizeLocale(nonBlankOr(props.getDefaultLocale(), "en"));
        boolean withPrefix = props.isPrefixDefaultLocale() || !locale.equals(defaultLocale);

        String url = withPrefix
                ? "%s/%s/%s".formatted(base, locale, path)
                : "%s/%s".formatted(base, path);

        return url + "?token=" + URLEncoder.encode(safeToken, StandardCharsets.UTF_8);
    }

    /**
     * 로케일 선택 로직: 요청 > 사용자 선호 > 기본값
     * - 허용되지 않은 로케일이면 기본값으로 대체
     * - 항상 non-null 반환
     * - 반환값/비교값은 소문자-하이픈으로 정규화
     */
    private String pickLocale(PasswordResetProperties p, String req, String user) {
        String def = normalizeLocale(nonBlankOr(p.getDefaultLocale(), "en"));

        // allowedLocales 정규화 (null이면 기본값만 허용)
        List<String> allowedRaw = p.getAllowedLocales();
        List<String> allowed = new ArrayList<>();
        if (allowedRaw == null || allowedRaw.isEmpty()) {
            allowed.add(def);
        } else {
            for (String a : allowedRaw) {
                if (a != null && !a.isBlank()) {
                    allowed.add(normalizeLocale(a));
                }
            }
            if (allowed.isEmpty()) allowed.add(def);
        }

        String candidate = normalizeLocale(firstNonBlankOrDefault(def, req, user, p.getDefaultLocale()));
        return allowed.contains(candidate) ? candidate : def;
    }

    private static String firstNonBlankOrDefault(String defaultVal, String... vals) {
        if (vals != null) {
            for (String v : vals) {
                if (v != null && !v.isBlank()) return v;
            }
        }
        return defaultVal;
    }

    private static String nonBlankOr(String val, String fallback) {
        return (val != null && !val.isBlank()) ? val : fallback;
    }

    private static String requireNonBlank(String val, String name) {
        if (val == null || val.isBlank()) {
            throw new IllegalStateException("PasswordResetProperties: required property '" + name + "' is missing or blank");
        }
        return val;
    }

    private static String normalizeLocale(String s) {
        // en, en-US, ko_KR 등 -> 소문자/하이픈
        String x = nonBlankOr(s, "en");
        return x.replace('_', '-').toLowerCase();
    }

    private static String stripTrailing(String s, char ch) {
        return (s != null && s.endsWith(String.valueOf(ch))) ? s.substring(0, s.length() - 1) : s;
    }

    private static String stripLeading(String s, char ch) {
        return (s != null && s.startsWith(String.valueOf(ch))) ? s.substring(1) : s;
    }
}