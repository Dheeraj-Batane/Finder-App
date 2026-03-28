package com.db.admin.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface ServiceProviderService {
    public void verifyProvider(Long providerId) throws MessagingException, UnsupportedEncodingException;
}
