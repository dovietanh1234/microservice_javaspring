package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.AuthenticationRequest;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    public boolean authenticate(AuthenticationRequest authenticationRequest){
        User user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow( () ->  new AppException(ErrorCode.USER_NOTFOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());


    }
}
