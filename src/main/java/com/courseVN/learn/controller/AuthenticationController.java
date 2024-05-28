package com.courseVN.learn.controller;

import com.courseVN.learn.dto.request.ApiResponse;
import com.courseVN.learn.dto.request.AuthenticationRequest;
import com.courseVN.learn.dto.response.AuthenticationResponse;
import com.courseVN.learn.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
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
       boolean res = authenticationService.authenticate( authenticationRequest );
       return ApiResponse.<AuthenticationResponse>builder()
               .code(200)
               .message("successfully")
               .result( AuthenticationResponse.builder()
                       .isAuthenticated(res)
                       .build() )
               .build();
    }

}
