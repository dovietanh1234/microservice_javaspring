package com.courseVN.learn.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// neu chi co request internal
//@Component // init cho no la 1 cai bean -> cai Fein client auto se duoc them vao IOC container
// luc nay cai Fein Client no se auto quet cai Bean nao ntn no se add vao and auto su dung.

// Neu co request ra ngoai pham vi microservice
// ko khai bao la 1 bean -> ma chung ta se config thu cong cho tung cai client mot
// -> tac dung: khi ta tao 1 bean no se anh huong den toan bo du an cua ta
// khi ta guu request ra ben ngoai gay ra tinh trang anh huong toi cac request ngoai pham vi microservice
@Slf4j
public class AuthenticationRequestInterceptor implements RequestInterceptor { // implement 1 cai interface cua Fein Client

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // Ta se modify cai request truoc khi chung ta guu di

        // b1: get cai header -> trong request ra!

        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

        log.info("Header {}", authHeader);

        // se co 1 so request ko co token nen ta can check no:
        // b2: them vao request truoc khi ta guu di.
        if(StringUtils.hasText(authHeader)){
            // Neu nhu ma CO GIA TRI them cai cai "Authorization" vao header cua chung ta va guu di
            requestTemplate.header("Authorization", authHeader);
        }

// => ta ko can phai add thu cong cai Authorization cua header. vao cai request nao ca



    }



}
