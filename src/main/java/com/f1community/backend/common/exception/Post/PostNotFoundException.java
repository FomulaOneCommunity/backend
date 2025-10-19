package com.f1community.backend.common.exception.Post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super(ErrorMessages.POST_NOT_FOUND);
    }
}
