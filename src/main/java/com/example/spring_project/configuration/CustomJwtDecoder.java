package com.example.spring_project.configuration;

import com.example.spring_project.dto.request.IntrospectRequest;
import com.example.spring_project.repository.InvalidatedTokenRepository;
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

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

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
        // ⭐ Bước 1: Parse và verify signature
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtSignerKey.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new JwtException("Invalid token signature");
        }

        // ⭐ Bước 2: Check blacklist (invalidated tokens)
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidatedTokenRepository.existsById(jwtId)) {
            throw new JwtException("Token has been revoked");
        }

        // ⭐ Bước 3: KHÔNG check expiry ở đây
        // Để NimbusJwtDecoder tự động check và throw proper exception

    } catch (ParseException | JOSEException e) {
        throw new JwtException("Token validation failed: " + e.getMessage());
    }

    // Build NimbusJwtDecoder để decode và validate expiry
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

    // ⭐ NimbusJwtDecoder sẽ tự động check expiry
    // Nếu expired → throw JwtException → 401 → frontend auto refresh
    return nimbusJwtDecoder.decode(token);
}
}
