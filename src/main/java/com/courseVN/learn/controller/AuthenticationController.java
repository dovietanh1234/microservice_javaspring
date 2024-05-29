package com.courseVN.learn.controller;

import com.courseVN.learn.dto.request.IntrospectRequest;
import com.courseVN.learn.dto.response.ApiResponse;
import com.courseVN.learn.dto.request.AuthenticationRequest;
import com.courseVN.learn.dto.response.AuthenticationResponse;
import com.courseVN.learn.dto.response.IntrospectResponse;
import com.courseVN.learn.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
