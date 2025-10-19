package com.f1community.backend.common.exception.Admin;

public class AdminUserNotFoundException extends RuntimeException {
    public AdminUserNotFoundException() {
        super(AdminErrorMessages.USER_NOT_FOUND);
    }
}