package com.f1community.backend.post.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super(ErrorMessages.POST_NOT_FOUND);
    }
}
