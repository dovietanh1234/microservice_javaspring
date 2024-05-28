package com.courseVN.learn.exception;


public class AppException extends RuntimeException{ // xu ly runtimeException vs Enum user existed
    private ErrorCode errorCode; //them thk nay vao de thk  RuntimeException co them thuoc tinh cua ta dua vao

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
