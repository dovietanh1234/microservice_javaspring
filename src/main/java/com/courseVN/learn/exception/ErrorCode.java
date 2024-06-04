package com.courseVN.learn.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED (1401, "user existed", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION( 1405, "Error! has error occur", HttpStatus.INTERNAL_SERVER_ERROR), // thong thuong httpStatus ko xac dinh dc
    USER_NOTFOUND (1404, "user not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1401, "username must be at least 8 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1401, "password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1401, "invalid message key!", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1401, "unauthenticated!", HttpStatus.UNAUTHORIZED), // 401
    UNAUTHORIZED(1401, "you do not have permission", HttpStatus.FORBIDDEN ), // 403
    INVALID_DOB(1400, "you are not old enough", HttpStatus.BAD_REQUEST ) // 403
    ;
    private int code;
    private HttpStatusCode httpStatusCode;
    private String message;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
