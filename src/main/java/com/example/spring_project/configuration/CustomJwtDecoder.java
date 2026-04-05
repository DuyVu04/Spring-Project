package com.example.spring_project.configuration;

import com.example.spring_project.dto.request.IntrospectRequest;
import com.example.spring_project.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
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
public class CustomJwtDecoder  implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String jwtSignerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder =null;

    //    @Override
//    public Jwt decode (String token){
//        try {
//            var result = authenticationService.introspect(IntrospectRequest.builder().token(token).build());
//            if(!result.isValid()){
//                throw new JwtException("Invalid token");
//            }
//        } catch (ParseException | JOSEException e) {
//            throw new JwtException(e.getMessage());
//        }
//        if(Objects.isNull(nimbusJwtDecoder)){
//            SecretKeySpec secretKeySpec =new SecretKeySpec(jwtSignerKey.getBytes(), "HmacSHA512");
//            nimbusJwtDecoder=NimbusJwtDecoder.withSecretKey(secretKeySpec)
//                    .macAlgorithm(MacAlgorithm.HS512)
//                    .build();
//        }
//        System.out.println(nimbusJwtDecoder);
//        return nimbusJwtDecoder.decode(token);
//    }
    @Override
    public Jwt decode(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(jwtSignerKey.getBytes());

            if (!signedJWT.verify(verifier)) {
                throw new JwtException("Invalid token signature");
            }

            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            if (authenticationService.isTokenBlacklisted(jwtId)) {
                throw new JwtException("Token has been revoked");
            }


        } catch (ParseException | JOSEException e) {
            throw new JwtException("Token validation failed: " + e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    jwtSignerKey.getBytes(),
                    "HmacSHA512"
            );
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }

    public org.springframework.security.core.Authentication decodeAndAuthenticate(String token) {
        try {

            var introspectResult = authenticationService.introspect(IntrospectRequest.builder().token(token).build());

            if (!introspectResult.isValid()) {
                throw new AuthenticationServiceException("Token validation failed");
            }


            Jwt jwt = decode(token);


            String username = jwt.getSubject();

            var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    java.util.Collections.emptyList()
            );

            return authentication;
        } catch (Exception e) {
            throw new AuthenticationServiceException("JWT validation failed: " + e.getMessage());
        }
    }
}
