package com.f1community.backend.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 🟥 Authentication / Registration
    EMAIL_ALREADY_EXISTS("This email is already in use."),
    USERNAME_ALREADY_EXISTS("This username '%s' is already in use."),
    INCORRECT_PASSWORD("The password does not match."),
    INVALID_PASSWORD("The password format is invalid."),
    PASSWORD_TOO_SHORT("Password must be at least 12 characters long."),
    PASSWORD_MISSING_REQUIREMENTS("Password must include uppercase, lowercase, number, and special character."),
    PASSWORD_CONTAINS_FORBIDDEN_WORD("Password contains too easy word: %s"),
    PASSWORD_CONTAINS_USER_INFO("Password cannot contain user information (name, email, birth date, etc.)."),
    USER_NOT_FOUND("User '%s' not found."),
    MAIL_AUTH_FAILED("Failed to authenticate with the mail server. Please contact the administrator."),

    // 🟩 File / Image Upload
    PROFILE_IMAGE_DIR_CREATE_FAILED("Failed to create profile image directory."),
    PROFILE_IMAGE_UPLOAD_FAILED("Failed to upload profile image."),
    FILE_SIZE_EXCEEDED("File size exceeded. Maximum allowed is 10MB."),

    // 🟦 Post
    POST_NOT_FOUND("Post not found."),

    // 🟨 Common
    INVALID_TOKEN("The token is invalid or has expired."),
    GENERIC_ERROR("A server error has occurred.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(this.message, args);
    }
}