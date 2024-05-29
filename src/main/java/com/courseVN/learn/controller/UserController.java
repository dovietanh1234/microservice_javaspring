package com.courseVN.learn.controller;

import com.courseVN.learn.dto.response.ApiResponse;
import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.dto.response.UserResponse;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
   ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){ //@Valid ta can validate cai object nay theo cai rule dc define trong object
        ApiResponse<User> response = new ApiResponse<>();
        response.setMessage("create user successfully");
        response.setResult( userService.createRequest(request) );
        return response;
    }
    // thk nay se tra ve exception: MethodArgumentNotValidException -> bat thang nay trong handle exception

    @GetMapping("/get")
    List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/detail/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId){
        return userService.getUserDetail(userId);
    }

    @PutMapping("/update/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest userUpdateRequest){
        return userService.updateUser( userId, userUpdateRequest );
    }

    @GetMapping("/delete/{userId}")
    String deleteUser(@PathVariable("userId") String userId){
        return userService.deleteUser( userId );
    }

}
