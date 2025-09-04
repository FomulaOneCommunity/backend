package com.f1community.backend.util;

public interface EmailService {
    // 기존 3-인자
    void sendPasswordResetEmail(String to, String subject, String resetLink);

    // 이름 한 덩어리로 받는 4-인자
    void sendPasswordResetEmail(String to, String subject, String resetLink, String fullName);

    // (호환용) first/last로 받는 5-인자: 인터페이스의 default 메서드로 제공
    default void sendPasswordResetEmail(String to, String subject, String resetLink,
                                        String firstName, String lastName) {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName  == null ? "" : lastName.trim();
        String fullName = (fn + " " + ln).trim();
        sendPasswordResetEmail(to, subject, resetLink, fullName.isEmpty() ? null : fullName);
    }
}