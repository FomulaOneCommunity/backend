package com.f1community.backend.user.exception;

import com.f1community.backend.common.exception.ErrorCode;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(ErrorCode.USER_NOT_FOUND.getMessage() + " (username: " + username + ")");
    }
}