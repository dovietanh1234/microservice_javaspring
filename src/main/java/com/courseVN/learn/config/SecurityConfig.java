package com.courseVN.learn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINT = {"/users", "/auth/token", "/auth/introspect"};

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests( request ->
                request
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                        // turn off authority in url -> to test method authority
                       // .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN") //.hasAuthority("SCOPE_ADMIN") custom cai SCOPE_ADMIN => sang cai khac ROLE_ADMIN
                       // .requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN") // -> khai bao kieu nay se tu nhien hon va robust hon rat nhieu so voi hasAuthority()
                        // vi no tu dong choc vao authorities va lay ra cac roles ben trong no.
                        .anyRequest().authenticated()
        );
        // them 1 buoc check jwt vao AuthenticationProvider chu ko tao mot filter rieng tuan thu theo Architecture security:
        httpSecurity.oauth2ResourceServer( oauth2 ->
                oauth2.jwt( jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()) //authenticate cho cai jwt cua chung ta generate
                       .jwtAuthenticationConverter(jwtAuthenticationConverter()) // config 1 cai bean converter jwt Authentication de sua lai cai Authority: "SCOPE_ADMIN" -> "ROLE_ADMIN"
                )
                // securityConfig cua spring no se cung cap cho chung ta 1 cai config de handle EXCEPTION 401:
                        .authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                // .authenticationEntryPoint() -> khi ma authentication fail no se dieu huong user di dau? -> return error message
                // .authenticationEntryPoint() -> no yc implement mot AuthenticationEntryPoint -> vi vay ta se tao 1 class de xu ly!
        );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
       return NimbusJwtDecoder
               .withSecretKey(secretKeySpec)
               .macAlgorithm(MacAlgorithm.HS512)
               .build()
               ;
    };

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){

        // B2 customize authority Mapper cho cai Converter nay:
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // CHUYEN SCOPE_ -> ROLE

        /*
        * setAuthoritiesClaimDelimiter() -> Claim name mac dinh la scope -> chuyen thanh cai khac!
        * setAuthoritiesPrefix() -> dau ngan cach giua cac scope la dau " " hay la ", " ...
        * setAuthoritiesClaimName()
        * */
        // B1
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    // init 1 cai @Bean Bcrypt vi no su dung kha nhieu noi:
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

}