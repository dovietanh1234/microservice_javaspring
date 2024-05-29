package com.courseVN.learn.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXISTED (1401, "user existed"),
    UNCATEGORIZED_EXCEPTION( 1405, "Error! has error occur" ),
    USER_NOTFOUND (1404, "user not found"),
    USERNAME_INVALID(1401, "username must be at least 8 characters"),
    PASSWORD_INVALID(1401, "password must be at least 8 characters"),
    INVALID_KEY(1401, "invalid message key!"),
    UNAUTHENTICATED(1401, "unauthenticated!")
    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
