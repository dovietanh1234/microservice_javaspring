package com.courseVN.learn.exception;

import com.courseVN.learn.dto.response.ApiResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// khai bao cho spring biet day la loi se handle all Exception -> sd annotation @ControllerAdvice
@ControllerAdvice
public class GlobalExeptionHandler {
    // define cac loai exception ta se bat o day. & tuong ung voi tung loai exception ta se tra ve the nao?

    // Tao mot Exception Bat all exception ma ngoai le! ko co trong nay
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<String>> handlingRuntimeException(Exception e){ // auto inject class through params
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(e.getMessage())
                .build());
    }
    /*
    * GlobalExeptionHandler -> quan ly catch all exceptions, each exception will have the error code & different message
    * so this code and message we need to get from exception
    * */

    // xu ly bang exception cua ta:
    @ExceptionHandler(value = AppException.class) // dua vao day class exception we want to catch
    ResponseEntity<ApiResponse<String>> handlingAppException(AppException e){  // AUTO INJECT PARAMETERS
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.badRequest().body( ApiResponse.<String>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(value = JOSEException.class)
    ResponseEntity<ApiResponse<String>> handlingJOSEException(JOSEException e){
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<String>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e){

        String enumKey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey); // neu nhu ma key word is misspelled
        }catch (IllegalArgumentException ex){
            // log ra thui
            System.out.println(ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());

        //return ResponseEntity.badRequest().body(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

}
