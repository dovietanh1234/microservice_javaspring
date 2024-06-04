package com.courseVN.learn.controller;

import com.courseVN.learn.dto.response.ApiResponse;
import com.courseVN.learn.dto.request.UserCreationRequest;
import com.courseVN.learn.dto.request.UserUpdateRequest;
import com.courseVN.learn.dto.response.UserResponse;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /*
    * tu custom cai Validate -> tu tao mot annotation
    *
    * nhan 1 request roi check o tang service or controller
    *
    * -> nhung no se can du lieu o nhieu noi & nhieu noi can validate
    *
    * Vay? 1 cach khac chung ta co the nghi den chung ta co the build duoc 1 cai custome annotaion duoc ko?
    * -> de chung ta co the validate o cac cai tang object.
    *
    * => tao 1 cai annotation de validate du lieu!
    *
    * */

    @PostMapping("/create")
   ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){ //@Valid ta can validate cai object nay theo cai rule dc define trong object
        return ApiResponse.<User>builder()
                .message("create user successfully")
                .result(userService.createRequest(request))
                .build();
    }
    // thk nay se tra ve exception: MethodArgumentNotValidException -> bat thang nay trong handle exception

    // method nay se chi nhung ai co role admin ms di vao:

    @GetMapping("/get")
    List<User> getUsers(){
    // thì trong spring để get cái thông tin mà hiện tại đang đăng nhập đang đc authenticate trong 1 cái request
    // ta sẽ sử dụng SecurityContextHolder. SecurityContextHolder -> sẽ chưa user dang dăng nhập hiện tại
       var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info( "username: {}", authentication.getName() );
       authentication.getAuthorities().forEach( grantedAuthority -> log.info( grantedAuthority.getAuthority() ) );
        // nghia la trong SecurityContextHolder khi ma AuthenticationProvider() == true no se gan user vao
        // trong SecurityContextHolder thi luc nay ta se goi den Authentication trong nay de lay ra Name cua doi tuong.

        // no se print Role ra la: SCOPE_USER
        // tai sao lai la SCOPE_USER: mac dinh JwtAuthenticationManager no se Map cai Scope
        // vao nhung cai Prefix "scope" -> no se dat la: "SCOPE_USER"

        return userService.getUsers();
    }

  //  @PostAuthorize("hasRole('ADMIN')")
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

    // chuyen token thông qua header sẽ lấy được thông tin của chính mình.
    @GetMapping("/myInfo")
    UserResponse getInfoByToken(){
        return userService.getMyInfo();
    }

}
