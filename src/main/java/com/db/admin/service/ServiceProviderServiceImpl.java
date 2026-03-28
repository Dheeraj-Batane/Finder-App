package com.db.admin.service;

import com.db.database.RepositoryFactory;
import com.db.database.entities.ServiceProvider;
import com.db.integration.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService{

    private @Autowired RepositoryFactory repositoryFactory;
    private @Autowired EmailService emailService;

    @Override
    public void verifyProvider(Long providerId) throws MessagingException, UnsupportedEncodingException {
        ServiceProvider provider = repositoryFactory.getServiceProviderRepository().findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found."));

        if ("VERIFIED".equalsIgnoreCase(provider.getStatus())) {
            throw new RuntimeException("Provider is already verified.");
        }

        provider.setStatus("VERIFIED");
        repositoryFactory.getServiceProviderRepository().save(provider);
        String providerEmail = provider.getUser().getEmail();
        String providerName = provider.getUser().getFirstName() + " " + provider.getUser().getLastName();

        // 3. Send the confirmation email
        emailService.sendProviderWelcomeEmail(providerEmail, providerName);
    }
}
