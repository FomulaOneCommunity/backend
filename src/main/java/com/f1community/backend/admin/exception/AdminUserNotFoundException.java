package com.f1community.backend.admin.exception;

public class AdminUserNotFoundException extends RuntimeException {
    public AdminUserNotFoundException() {
        super(AdminErrorMessages.USER_NOT_FOUND);
    }
}