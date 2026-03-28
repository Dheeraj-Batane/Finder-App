package com.db.auth.service;

import com.db.auth.dto.ProviderOnboardingRequest;
import com.db.auth.dto.SignUpRequest;
import com.db.auth.dto.SignUpResponse;
import com.db.database.entities.ServiceProvider;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface IAuthService {
    public SignUpResponse registerUser(SignUpRequest request);
    public ServiceProvider onboardProvider(ProviderOnboardingRequest request) throws MessagingException, UnsupportedEncodingException;
}
