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
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import jakarta.servlet.http.HttpServletResponse;
import com.example.spring_project.service.CookieService;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    CookieService cookieService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request,
                                                     HttpServletResponse response) {
        var result = authenticationService.authenticate(request);
        cookieService.setAccessTokenCookie(response, result.getToken());
        cookieService.setUserRoleCookie(response, result.getRole());
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody(required = false) RefreshTokenRequest request,
                                                     @CookieValue(name = "accessToken", required = false) String accessToken,
                                                     HttpServletResponse response)
            throws ParseException, JOSEException {

        String token = (request != null && request.getToken() != null) ? request.getToken() : accessToken;
        if (token == null)
            throw new CustomException(ErrorCode.UNAUTHENTICATED);

        if (request == null)
            request = RefreshTokenRequest.builder().token(token).build();
        else if (request.getToken() == null)
            request.setToken(token);

        var result = authenticationService.refreshToken(request);
        cookieService.setAccessTokenCookie(response, result.getToken());
        cookieService.setUserRoleCookie(response, result.getRole());
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @CookieValue(name = "accessToken", required = false) String token,
            HttpServletResponse response
    ) throws ParseException, JOSEException {

        if (token != null) {
            authenticationService.logout(
                    LogoutRequest.builder().token(token).build()
            );
        }

        cookieService.clearCookies(response);

        return ApiResponse.<Void>builder().build();
    }
}
