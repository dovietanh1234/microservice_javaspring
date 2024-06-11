package com.courseVN.learn.controller;

import com.courseVN.learn.dto.request.IntrospectRequest;
import com.courseVN.learn.dto.request.LogoutRequest;
import com.courseVN.learn.dto.request.RefreshTokenRequest;
import com.courseVN.learn.dto.response.ApiResponse;
import com.courseVN.learn.dto.request.AuthenticationRequest;
import com.courseVN.learn.dto.response.AuthenticationResponse;
import com.courseVN.learn.dto.response.IntrospectResponse;
import com.courseVN.learn.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
     ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){
       return ApiResponse.<AuthenticationResponse>builder()
               .code(200)
               .message("successfully")
               .result( authenticationService.authenticate( authenticationRequest ) )
               .build();
    }



    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> verify(@RequestBody IntrospectRequest introspectRequest){
        return ApiResponse.<IntrospectResponse>builder()
                .code(200)
                .message("successfully")
                .result(authenticationService.introspectToken( introspectRequest ))
                .build();
    }

    // RefreshTokenRequest
    // refreshToken(RefreshTokenRequest request)
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return ApiResponse.<AuthenticationResponse>builder()
                .message("refresh success")
                .code(200)
                .result( authenticationService.refreshToken(request) )
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest){

        System.out.println("co chay vao day ko?");
        authenticationService.logout(logoutRequest);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("logout successfully")
                .build();
    }

}
