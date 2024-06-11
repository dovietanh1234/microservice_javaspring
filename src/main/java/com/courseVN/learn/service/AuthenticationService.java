package com.courseVN.learn.service;

import com.courseVN.learn.dto.request.AuthenticationRequest;
import com.courseVN.learn.dto.request.IntrospectRequest;
import com.courseVN.learn.dto.request.LogoutRequest;
import com.courseVN.learn.dto.request.RefreshTokenRequest;
import com.courseVN.learn.dto.response.AuthenticationResponse;
import com.courseVN.learn.dto.response.IntrospectResponse;
import com.courseVN.learn.entity.InvalidatedToken;
import com.courseVN.learn.entity.User;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.repository.InvalidatedTokenRepository;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvalidatedTokenRepository _invalidatedTokenRepository;

    // bao ve cai nay dua vao file .yml
  //  @NonFinal     // ko inject cai nay vao constructor
   // protected static final String SIGNER_KEY = "1E4BEeshlG+55vB4ZdEBscAw4gEW0HqMeva26R8HyUBDr8jLW3XT68wuDONhlzyl";
    // khi dua secretKey vao .yml -> team devops ho deploy project cua minh len moi truong cao hon
    // chac chan ho se dung cai sign key khac de dam bao an toan

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected long REFRESH_DURATION;

  //  private static String SIGNER_KEY = "1E4BEeshlG+55vB4ZdEBscAw4gEW0HqMeva26R8HyUBDr8jLW3XT68wuDONhlzyl";

    public IntrospectResponse introspectToken(IntrospectRequest introspectRequest){
            String token = introspectRequest.getToken();
            // verify -> framework Nimbus provide us a class is JWSVerifier
           boolean isValid = true;
            try {
                verifyToken(token, false);
            }catch (AppException e){
                isValid = false;
            }
            return IntrospectResponse.builder()
                    .valid( isValid )
                    .build();
    }


    private SignedJWT verifyToken(String token, boolean isRefresh) {

        try {
        JWSVerifier verifier =  new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        // check xem het han chua?
        Date expiry_time = (isRefresh) ? // true -> verify refresh token | false -> verify token
          // lay ra thoi gian expire time -> issue time + time config refresh token enable
          new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESH_DURATION, ChronoUnit.SECONDS).toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime(); // verify token thi thoi gian khac. - verify refresh token thi thoi gian khac
        // check is token co phai la cua chung ta tao ra ko?
        var verify = signedJWT.verify(verifier);

            if(!(expiry_time.after( new Date() ) && verify)){
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            // neu ma token nay da ton tai trong DB return error
            // xu ly duoi nay vi neu nhu no da het hieu luc or expire no se vao EXCEPTION
            // neu no van con valid va da logout roi thi no ms nam trong kha nang tiem tan invalid token

            if(_invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return signedJWT;

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
                .expirationTime(new Date(Instant.now().plus( VALID_DURATION, ChronoUnit.SECONDS ).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString()) // mot chuoi ky tu ngau nhien ko trung nhau!
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

    // VI LA vua sua lai ROLES & PERMISSION vào trong Users
    private String buildScope(User user){ // built scope tu 1 user
        // vi la cai scope is a list so we use StringJoiner
        // boi vi cac cai scope trong oauth2 no quy dinh phan cach nhau bang dau cach
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
           // user.getRoles().forEach(s -> stringJoiner.add(s));

          // thay doi gia tri tu String -> entity roles
            user.getRoles().forEach( role -> {
                // hom truoc la co 1 list string h ta se sua lai
                // add roles:
                stringJoiner.add("ROLE_" + role.getName()); //Them tien to ROLE_ de phan bien role

                // check permission is null?
                if(!CollectionUtils.isEmpty( role.getPermissions() ))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));// add permissions:

            } );

            // thuong thi khi ta de nhieu role or permission trong role thi header chi nhan toi da 4kb
            // chung ta neu de nhieu qua se gay nang cai token.
        }

        return stringJoiner.toString();
    }

//SignedJWT của thư viện Nimbus được sử dụng để tạo và xác thực các JSON Web Tokens (JWT) đã ký
//SignedJWT của Nimbus là một công cụ mạnh mẽ và linh hoạt trong việc quản lý JWT
    public AuthenticationResponse refreshToken(RefreshTokenRequest request){

        // check xem token nay con exist or not:
        SignedJWT signedJWT = verifyToken(request.getToken(), true);

        // neu ma thanh cong: -> cap lai refresh token moi | invalid luon cai request.getToken():
        try {
            // B1 INVALID TOKEN CU DI
            var jit = signedJWT.getJWTClaimsSet().getJWTID();
            var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expireTime(expiryTime)
                    .build();

            _invalidatedTokenRepository.save(invalidatedToken);
            var username = signedJWT.getJWTClaimsSet().getSubject();

            User user = userRepository.findByUsername(username).orElseThrow(
                    ()-> new AppException(ErrorCode.UNAUTHENTICATED)
            );

            // build token dua tren thong tin user:
            String token = generateToken(user);

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();

        } catch (ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }


    public void logout(LogoutRequest request){
        //  khi logout ta se them 1 dong vao bang invalidateToken:

        // lay cai jwt token id ra:
        try {

            //b1 doc thong tin cua token  -> lay ra cai token id + thoi diem hết hạn
            var signToken = verifyToken(request.getToken(), true);
            // tai sao lai su dung time refresh token de logout?
            // neu ko check time refresh token thi token thi ta se nhan ngay exception "Date expiryTime();"
            // nghia la ta se luu ca refresh token vao bang invalidate token
            // chanh viec ho su dung lai cai token nay de lay refresh token moi.


            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            // sau do persist chuyen data va DB InvalidatedToken
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expireTime(expiryTime)
                    .build();

            _invalidatedTokenRepository.save(invalidatedToken);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        //

    }


}
