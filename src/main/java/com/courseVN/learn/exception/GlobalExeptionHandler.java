package com.courseVN.learn.exception;

import com.courseVN.learn.dto.response.ApiResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

/*
 token invalid -> 401 & ko co thong bao loi
* Những cái error NÓ XẢY RA TRÊN TẦNG FILTER trước khi vào cái service của chúng ta => CÁI GlobalException ko thể xử lý
*  được tình huống đó! => ta phai xư ly trong spring security Config.
* */

// khai bao cho spring biet day la loi se handle all Exception -> sd annotation @ControllerAdvice
@ControllerAdvice
public class GlobalExeptionHandler {
    // define cac loai exception ta se bat o day. & tuong ung voi tung loai exception ta se tra ve the nao?

    private static final String MIN_ATTRIBUTE = "min";

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

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body( ApiResponse.<String>builder()
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

        Map<String, Object> attributes = null;

        String enumKey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try {
            errorCode = ErrorCode.valueOf(enumKey); // neu nhu ma key word is misspelled


            // trong spring cai "exception" cung cap cai kha nang la chung ta co the lay duoc cai attribute ma chung ta
            // chuyen vao annotation:
            var constrainViolation = e.getBindingResult()
                    .getAllErrors().get(0).unwrap(ConstraintViolation.class); // unwrap cai nay ra de lay mot object ta mong muon
            // lay ra cac cai errors ma MethodArgumentNotValidException no wrap lai

             attributes = constrainViolation.getConstraintDescriptor().getAttributes(); // tu cai getAttributes lay duoc cai thong tin cua cai params chung ta chuyen vao

            // attribute = {..., min=18, ...};

            // binding don gian vao enum ErrorCode

        }catch (IllegalArgumentException ex){
            // log ra thui
            System.out.println(ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes) ? mapAttribute(errorCode.getMessage(), attributes) : errorCode.getMessage())
                .build());

        //return ResponseEntity.badRequest().body(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<String>> handlingAccessDeniedException(AccessDeniedException e){
        return ResponseEntity.status( ErrorCode.UNAUTHORIZED.getHttpStatusCode() ).body(ApiResponse.<String>builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(e.getMessage())
                .build());
    }


    // tao 1 ham de map gtri vao ErrorCode:
    private String mapAttribute(String message, Map<String, Object> attributes){


        // replace thong tin trong message goc thanh cac thong tin trong object
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE)) ;

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);

    }

}
