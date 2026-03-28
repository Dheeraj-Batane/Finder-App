package com.db.auth.service;

import com.db.auth.dto.ProviderOnboardingRequest;
import com.db.auth.dto.SignUpRequest;
import com.db.database.entities.ServiceProvider;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface IAuthService {
    public void registerUser(SignUpRequest request);
    public ServiceProvider onboardProvider(ProviderOnboardingRequest request) throws MessagingException, UnsupportedEncodingException;
}
