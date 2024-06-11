package com.courseVN.learn.repository.httpclient;


import com.courseVN.learn.config.AuthenticationRequestInterceptor;
import com.courseVN.learn.dto.request.ProfileCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
// attribute "configuration" se co 1 list cac class
// configuration = {AuthenticationRequestInterceptor.class} -> ket qua nhu cai class ta de @Component
// b1 khai bao 1 feignClient
@FeignClient(name = "profile-service", url="http://localhost:8081/profile", configuration = {AuthenticationRequestInterceptor.class}) // khai bao root endpoint
public interface ProfileClient { // khi ta muon su dung mot cai interceptor nao o dau thi ta se khai bao

    // thang nay se connect toi profile service
    @PostMapping(value = "/internal/user", produces = MediaType.APPLICATION_JSON_VALUE) //produces -> tao ra metadata la JSON
    Object createProfile(
            @RequestBody ProfileCreateRequest request);
}

/*
* de thuc hien 1 cai Http request:
* 1. can 1 dia tri endpoint
* 2. goi den 1 API
*  -> method Post or Get
*
*
* */
