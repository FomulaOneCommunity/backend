package com.f1community.backend.user.exception;

import com.f1community.backend.common.exception.ErrorCode;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage() + " (" + username + ")");
    }
}