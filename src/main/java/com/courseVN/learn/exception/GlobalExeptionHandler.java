package com.courseVN.learn.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

// khai bao cho spring biet day la loi se handle all Exception -> sd annotation @ControllerAdvice
@ControllerAdvice
public class GlobalExeptionHandler {
    // define cac loai exception ta se bat o day. & tuong ung voi tung loai exception ta se tra ve the nao?

    @ExceptionHandler(value = RuntimeException.class) // dua vao day class exception we want to catch
    ResponseEntity<String> handlingRuntimeException(RuntimeException e){ // khi khai bao vao tham so cua method
        // spring no se inject exception nay vao tham so -> lay thong tin ra va xu ly thui
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<String> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.badRequest().body(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

}
