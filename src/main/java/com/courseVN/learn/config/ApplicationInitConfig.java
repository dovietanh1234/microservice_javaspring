package com.courseVN.learn.config;

import com.courseVN.learn.entity.User;
import com.courseVN.learn.enums.Roles;
import com.courseVN.learn.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
public class ApplicationInitConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        /*
        ApplicationRunner la an interface
        * applicationRunner -> no se duoc khoi chay khi cai application cua chung ta chay
        * */
        return args -> {
            // mink se check truoc xem cai account admin is exist or not if it's not exist we will init
           if( userRepository.findByUsername("admin").isEmpty()) {
               var roles = new HashSet<String>();
               roles.add(Roles.ADMIN.name());
               User u = User.builder()
                       .username("admin")
                       .password(passwordEncoder.encode("admin"))
                      // .roles(roles)
                       .build();

               userRepository.save(u);
               log.warn("admin user has been created with default password: admin, please change it ");

           }
        };
    }

}
