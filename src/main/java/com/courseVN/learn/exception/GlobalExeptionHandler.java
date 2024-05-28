package com.courseVN.learn.exception;

import com.courseVN.learn.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

// khai bao cho spring biet day la loi se handle all Exception -> sd annotation @ControllerAdvice
@ControllerAdvice
public class GlobalExeptionHandler {
    // define cac loai exception ta se bat o day. & tuong ung voi tung loai exception ta se tra ve the nao?

    // Tao mot Exception Bat all exception ma ngoai le! ko co trong nay
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<String>> handlingRuntimeException(Exception e){ // auto inject class through params
       ApiResponse<String> res = new ApiResponse<>();
       res.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
       res.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() );
        return ResponseEntity.badRequest().body(res);
    }
    /*
    * GlobalExeptionHandler -> quan ly catch all exceptions, each exception will have the error code & different message
    * so this code and message we need to get from exception
    * */

    // xu ly bang exception cua ta:
    @ExceptionHandler(value = AppException.class) // dua vao day class exception we want to catch
    ResponseEntity<ApiResponse<String>> handlingAppException(AppException e){  // AUTO INJECT PARAMETERS
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse<String> res = new ApiResponse<>();
        res.setCode(errorCode.getCode());
        res.setMessage(errorCode.getMessage() );
        return ResponseEntity.badRequest().body(res);
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


//Phương thức valueOf() được sử dụng để chuyển đối kiểu dữ liệu khác thành kieu du lieu cua no
        ApiResponse<String> res = new ApiResponse<>();

        res.setCode(errorCode.getCode());
        res.setMessage(errorCode.getMessage() );
        return ResponseEntity.badRequest().body(res);

        //return ResponseEntity.badRequest().body(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

}
