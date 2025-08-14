package com.example.spring_project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION (9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTS(1002, "User already exists",HttpStatus.BAD_REQUEST),
    USER_NOTEXISTS(1005, "User not exists",HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1003, "Username must be between 3 and 10 characters",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated",HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1001, "Uncategorized exception",HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "You do not have permission",HttpStatus.FORBIDDEN);


    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;


    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
