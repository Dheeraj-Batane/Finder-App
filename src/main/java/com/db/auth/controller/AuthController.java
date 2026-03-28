package com.db.auth.controller;

import com.db.auth.dto.*;
import com.db.auth.service.IAuthService;
import com.db.common.Constants;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {
        try {
            SignUpResponse signUpResponse = authService.registerUser(signUpRequest);
            return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/provider/onboard")
    public ResponseEntity<String> onboardProvider(@Valid @RequestBody ProviderOnboardingRequest request) {
        try {
            authService.onboardProvider(request);
            return new ResponseEntity<>("Service Provider onboarded successfully!", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Catches validation errors (like invalid user ID, wrong role, or duplicate onboarding)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request){
        LoginResponse loginUser = authService.loginUser(request);
        if(Constants.SUCCESS_CODE.equalsIgnoreCase(loginUser.getResponseCode())){
        return ResponseEntity.status(HttpStatus.OK).body(loginUser);
         }
        else{
        return ResponseEntity.ok(loginUser);
         }
    }

}
