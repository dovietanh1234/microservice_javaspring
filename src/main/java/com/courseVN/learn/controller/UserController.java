package com.courseVN.learn.controller;

import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/user")
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/create")
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createRequest(request);
    }

    @GetMapping("/get")
    List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/detail/{userId}")
    User getUser(@PathVariable("userId") String userId){
        return userService.getUserDetail(userId);
    }

    @PutMapping("/update/{userId}")
    User updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest userUpdateRequest){
        return userService.updateUser( userId, userUpdateRequest );
    }

    @GetMapping("/delete/{userId}")
    String deleteUser(@PathVariable("userId") String userId){
        return userService.deleteUser( userId );
    }

}
