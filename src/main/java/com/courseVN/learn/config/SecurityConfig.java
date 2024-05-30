package com.courseVN.learn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINT = {"/users", "/auth/token", "/auth/introspect"};

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests( request ->
                request
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                       // .requestMatchers(HttpMethod.POST, "/auth/token", "/auth/introspect").permitAll()
                        .anyRequest().authenticated()
        ); // lamba function-> trien khai function in interface B1

        // Config phai co JWT thi moi chay vao cac endpoint bi filter chan:
        httpSecurity.oauth2ResourceServer( oauth2 ->
                // config jwt
                oauth2.jwt( jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()) ) //authenticate cho cai jwt cua chung ta generate
                // B3: .decoder() -> La mot interface. tac dung: giup decode cai token ta chuyen vao,
                // vi la 1 interface nen chung ta phai can 1 cai trien khai cho interface do.

                /*
                vi we dang sd cai oauth2 resource server -> vi vay o day se co 1 so cai method cau hinh:
                ex 1:
                * oauth2.jwt( jwtConfigurer -> jwtConfigurer.jwkSetUri("");
                neu chung ta cau hinh vs 1 resource server thu 3 thi ta co uri nay cung cap vao la xong!

     => Ket Luan: khi chung ta dang ky oauth2ResourceServer la chung ta muon dang ky mot cai
     AuthenticationProvider De ho tro cho cai JWT Token
     Sau khi co jwt trong AuthenticationProvider ->
      thk nay no se decoder jwt bang jwtDecoder() ta da cau hinh cho no. de biet hop le hay ko hop le.

                 * */
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable); // <- ap dung sort hand syntax lamba method
        // B2 .csrf( httpSecure -> httpSecure.disable()  ) <- normal syntax lamba



        return httpSecurity.build();

    }


    // B4: trien khai implement cho interface decoder:
    @Bean
    JwtDecoder jwtDecoder(){
        // tao 1 secretKey:
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512"); //my secretKey and thuat toan
        // nho vao lai file Authentication Service de xem lai thuat toan da ma hoa secretKey

          // cung cap 1 cai jwt o day:
       return NimbusJwtDecoder
               .withSecretKey(secretKeySpec)
               .macAlgorithm(MacAlgorithm.HS512)
               .build()
               ; // truong hop cua chung ta sd vs secret-key -> yc secret-key
        /*
        * Spring security cx sd mot cai NimbusDecoder( framework installed )
        * va vi oauth2 spring security no da co san NimbusJwtDecoder nen ta ko phai cai Nimbus rieng nua
        *
        * NimbusJwtDecoder co nhieu cai builter trong no:
        * NimbusJwtDecoder
        *           .withIssuerLocation("")
        *           .withJwtSetUri("")
        *           .withPublicKey("")
        * ... day la nhung ham lien quan den oauth2 server
        *
        * */
    }; //=> ta se co 1 cai Bean cua jwt decoder






    /*
    * mot trong nhung cai cau hinh co ban nhat dau tien ta se cau hinh:
    * Cac endpoint nao se duoc phep truy cap o pham vi public
    *
    *tai sao phai cau hinh csrf -> spring security default turn on CSRF Config -> bao ve cac endpoint cua ban truoc cac attack cross site
    * Neu ko tat no di thi mac du de permitAll() nhung ko the chay dc
    * */

}


/*
* Hieu ro hon ve lamba:
* use Comparator:
*
* listDevs.sort(new Comparator<Developer>() {
	@Override
	public int compare(Developer o1, Developer o2) {
		return o2.getAge() - o1.getAge();
	}
});
*
* use Lamba
*
* listDevs.sort((Developer o1, Developer o2)->o1.getAge()-o2.getAge());
*
* => Lamba ko phai khoi tao instance ko phai override gi ca cu the viet luon & truyen tham so.
*
* */