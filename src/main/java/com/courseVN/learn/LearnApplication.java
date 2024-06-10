package com.courseVN.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // -> muc dich: quet cac khai bao cua chung ta de no inject vao de no quet toan bo pham vi du an
public class LearnApplication {
	public static void main(String[] args) {
		SpringApplication.run(LearnApplication.class, args);
	}

}

/*
* khai bao OpenFeign o tang nao service controller repository
* OpenFeign -> thuc chat no di fetch data o dau do ve! -> la nguon cung cap data cho service
* => OpenFeign se nam o tang repository
*
*
* */
