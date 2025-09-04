package com.f1community.backend.user.exception;

import com.f1community.backend.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ImageUploadException extends RuntimeException {
    private final ErrorCode errorCode;

    public ImageUploadException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

}