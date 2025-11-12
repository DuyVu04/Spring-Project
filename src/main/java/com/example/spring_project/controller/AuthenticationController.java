package com.example.spring_project.controller;


import com.example.spring_project.dto.request.AuthenticationRequest;
import com.example.spring_project.dto.request.IntrospectRequest;
import com.example.spring_project.dto.request.LogoutRequest;
import com.example.spring_project.dto.request.RefreshTokenRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.AuthenticationResponse;
import com.example.spring_project.dto.response.IntrospectResponse;
import com.example.spring_project.dto.response.RefreshTokenResponse;
import com.example.spring_project.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result= authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }



    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException , JOSEException {
        var result= authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException , JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Logout successful")
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<RefreshTokenResponse> refreshToken (@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<RefreshTokenResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }
}
