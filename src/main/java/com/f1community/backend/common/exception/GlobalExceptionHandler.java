package com.f1community.backend.common.exception;

import com.f1community.backend.admin.exception.AdminUserNotFoundException;
import com.f1community.backend.common.response.ErrorResponse;
import com.f1community.backend.post.exception.PostNotFoundException;
import com.f1community.backend.user.exception.*;
import jakarta.mail.AuthenticationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ErrorResponse.of(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ErrorResponse.of(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleIncorrectPassword(IncorrectPasswordException ex) {
        return ErrorResponse.of(ErrorCode.INCORRECT_PASSWORD);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidPassword(InvalidPasswordException ex) {
        return ErrorResponse.of(ErrorCode.INVALID_PASSWORD);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return ErrorResponse.of(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(AdminUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAdminUserNotFound(AdminUserNotFoundException ex) {
        return ErrorResponse.of(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePostNotFound(PostNotFoundException ex) {
        return ErrorResponse.of(ErrorCode.POST_NOT_FOUND);
    }

    @ExceptionHandler(MailAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleMailAuth(MailAuthenticationException ex) {
        ErrorResponse body = new ErrorResponse(ErrorCode.MAIL_AUTH_FAILED);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleJakartaMailAuth(AuthenticationFailedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(ErrorCode.MAIL_AUTH_FAILED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(ErrorCode.GENERIC_ERROR));
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidResetToken(InvalidResetTokenException ex) {
        return ErrorResponse.of(ErrorCode.INVALID_TOKEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return ErrorResponse.of(ErrorCode.INVALID_TOKEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE) // 413
    public ErrorResponse handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return ErrorResponse.of(ErrorCode.FILE_SIZE_EXCEEDED);
    }
}