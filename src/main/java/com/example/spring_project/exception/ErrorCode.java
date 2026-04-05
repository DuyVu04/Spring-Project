package com.example.spring_project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION (9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTS(1002, "User already exists",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1005, "User not exists",HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1003, "Username must be between 3 and 10 characters",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated",HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1001, "Uncategorized exception",HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_NOT_VALID(1008,"Method argument not valid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "You do not have permission",HttpStatus.FORBIDDEN),
    COURSE_NOT_FOUND(1009, "Course not found",HttpStatus.NOT_FOUND),
    MODULE_NOT_FOUND(1010, "Module not found",HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(1011, "Lesson not found",HttpStatus.NOT_FOUND),
    ENROLLMENT_NOT_FOUND(1012, "Enrollment not found",HttpStatus.NOT_FOUND),
    ENROLLMENT_ALREADY_EXISTS(1013, "Enrollment already exists",HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXISTS(1014, "Review already exists",HttpStatus.BAD_REQUEST),
    WISHLIST_ALREADY_EXISTS(1015, "Wishlist already exists",HttpStatus.BAD_REQUEST),
    WISHLIST_NOT_FOUND(1016, "Wishlist not found",HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(1017, "Review not found",HttpStatus.NOT_FOUND),
    DISCUSSION_NOT_FOUND(1018, "Discussion not found",HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(1019, "Resource not found",HttpStatus.NOT_FOUND),
    RESOURCE_EXISTS(1020, "Resource already exists",HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1021, "Access denied",HttpStatus.FORBIDDEN),
    INVALID_INPUT(1022, "Invalid input",HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_EXISTS(1023, "Friend request already exists", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_NOT_FOUND(1024, "Friendship not found", HttpStatus.NOT_FOUND),
    CANNOT_FRIEND_SELF(1025, "Cannot friend yourself", HttpStatus.BAD_REQUEST),
    PREMIUM_REQUIRED(1026, "This course requires a premium membership", HttpStatus.FORBIDDEN);
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;


    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
