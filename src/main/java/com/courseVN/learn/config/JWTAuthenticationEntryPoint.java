package com.courseVN.learn.config;

import com.courseVN.learn.dto.response.ApiResponse;
import com.courseVN.learn.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
// ctrl i -> de implement cac method trong interface vao!
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // commence se co 1 exception xay ra trong qua trinh authenticate -> thuong se la exception ko thanh cong
        // b1: object response -> day la chung ta response ve giao dien nguoi dung.
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getHttpStatusCode().value()); // 1
        // content type:
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 2 cai nay quan trong! vi de no tra ve de consumer cua ta biet no tra ve cai j

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();


        response.getWriter().write(objectMapper.writeValueAsString(apiResponse)); // 3 convert api ve string
        // write yc 1 string nhung cua ta dang la object -> ta can sd den object mapper de chung ta convert
        // object thanh 1 json...

        //** sau khi config day du ta se commit cai response nay ve:
        response.flushBuffer(); // force gửu request ve cho client
    }
}
