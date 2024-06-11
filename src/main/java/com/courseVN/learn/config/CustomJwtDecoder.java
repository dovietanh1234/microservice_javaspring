package com.courseVN.learn.config;

import com.courseVN.learn.dto.request.IntrospectRequest;
import com.courseVN.learn.exception.AppException;
import com.courseVN.learn.exception.ErrorCode;
import com.courseVN.learn.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

  //  private String signerKey = "1E4BEeshlG+55vB4ZdEBscAw4gEW0HqMeva26R8HyUBDr8jLW3XT68wuDONhlzyl";

    private AuthenticationService _authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        /* NEU KO CO Microservices thi can phai verify token in here:

        // b1 check xem token nay con valid hay ko? neu ko tra ve exception
             var response = _authenticationService.introspectToken(IntrospectRequest.builder()
                        .token(token)
                        .build());

             if (response.isValid()){
                 throw new JwtException("token invalid");
             }
            // b2 neu nhu token con valid thi ta se delegate cho cai NimbusJwtDecoder
        // de no thuc hien cai viec XAC THUC TOKEN & BUILD CAI JWT THEO Y/C cua spring security
        // trước khi verify token minh se check xem no da bi het han hay chua hay da bi logout chua neu roi thi tha exception.
        if(Objects.isNull(nimbusJwtDecoder)){
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);

        * */

        // Xu ly khi co microservices
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return new Jwt(token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(), // issuer la mot cai date
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims()
                    );
            // ta da build xong duoc JWT de chung ta tra ve cho spring security
        } catch ( ParseException e) {
            throw new JwtException("invalid token");
        }
/*
* => vay la chung ta config cai spring security -> chung ta thay doi tuy chinh trong nay
* ta vua set dc role van co the su dung dc all tinh nang cua spring security de chung ta co the sd sau nay
* */



    }


}
