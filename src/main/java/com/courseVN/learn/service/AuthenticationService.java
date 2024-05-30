package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.AuthenticationRequest;
import com.courseVN.learn.dto.request.IntrospectRequest;
import com.courseVN.learn.dto.response.AuthenticationResponse;
import com.courseVN.learn.dto.response.IntrospectResponse;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.Valid;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    // bao ve cai nay dua vao file .yml
  //  @NonFinal     // ko inject cai nay vao constructor
   // protected static final String SIGNER_KEY = "1E4BEeshlG+55vB4ZdEBscAw4gEW0HqMeva26R8HyUBDr8jLW3XT68wuDONhlzyl";
    // khi dua secretKey vao .yml -> team devops ho deploy project cua minh len moi truong cao hon
    // chac chan ho se dung cai sign key khac de dam bao an toan

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;


    public IntrospectResponse introspectToken(IntrospectRequest introspectRequest){
            String token = introspectRequest.getToken();
            // verify -> framework Nimbus provide us a class is JWSVerifier

        try {
            JWSVerifier verifier =  new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            // check xem het han chua?
            Date expiry_time = signedJWT.getJWTClaimsSet().getExpirationTime();
            // check is token co phai la cua chung ta tao ra ko?
           var verify = signedJWT.verify(verifier); // return true or false

            return IntrospectResponse.builder()
                    .valid( expiry_time.after( new Date() ) && verify )
                    .build();

        } catch (JOSEException | ParseException e) {
            throw new AppException( ErrorCode.UNAUTHENTICATED );
        }

    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest){
        User user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow( () ->  new AppException(ErrorCode.USER_NOTFOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean isValid = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if( !isValid ){
            throw new AppException(  ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .isAuthenticated(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))
                .token(token)
                .build();


    }

    // create a method to create a token:
    private String generateToken( User user ){

        //1. create header ->encode algorithm
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        //2. de built đc payload ta can co khai niem la "Claims" data in body no duoc goi la Claims
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("vietanh")
                .issueTime( new Date())
                .expirationTime(new Date(Instant.now().plus( 1, ChronoUnit.HOURS ).toEpochMilli()))
                .claim("scope", buildScope(user)) // we config a claim and set roles inside by to building a func get roles
                // so to put the roles inside we need to config a function:
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
       //3. build token
        JWSObject jwsObject = new JWSObject(header, payload); // 2 params header & payload

        //4. sign token:
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException( ErrorCode.UNAUTHENTICATED );
        }
    }

    private String buildScope(User user){ // built scope tu 1 user
        // vi la cai scope is a list so we use StringJoiner
        // boi vi cac cai scope trong oauth2 no quy dinh phan cach nhau bang dau cach
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
           // user.getRoles().forEach(s -> stringJoiner.add(s));
            user.getRoles().forEach(stringJoiner::add);
        }

        return stringJoiner.toString();

    }




}
