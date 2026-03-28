package com.db.auth.service;

import com.db.auth.dto.ProviderOnboardingRequest;
import com.db.auth.dto.ScheduleRequest;
import com.db.auth.dto.SignUpRequest;
import com.db.auth.dto.SignUpResponse;
import com.db.common.Constants;
import com.db.database.RepositoryFactory;
import com.db.database.entities.*;
import com.db.integration.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;

@Service
public class AuthServiceImpl implements IAuthService{

    @Autowired private RepositoryFactory repositoryFactory;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    @Override
    public SignUpResponse registerUser(SignUpRequest request) {
        if (repositoryFactory.getUserRepository().existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setCellPhone(request.getCellPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = repositoryFactory.getRolesRepository().findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRole(userRole);

        // Map the address from the request
        Address newAddress = new Address();
        newAddress.setAddressLine1(request.getAddress().getAddressLine1());
        newAddress.setAddressLine2(request.getAddress().getAddressLine2());
        newAddress.setCity(request.getAddress().getCity());
        newAddress.setState(request.getAddress().getState());
        newAddress.setZipCode(request.getAddress().getZipCode());
        newAddress.setAddressType(request.getAddress().getAddressType());
        newAddress.setVersion(0L);
        // Use the helper method we created earlier to keep the relationship in sync
        user.addAddress(newAddress);

        // Saving the user will cascade and save the address to the addresses table
        User save = repositoryFactory.getUserRepository().save(user);
        SignUpResponse signUpResponse=new SignUpResponse();
        signUpResponse.setResponseCode(Constants.SUCCESS_CODE);
        signUpResponse.setUserId(String.valueOf(save.getId()));
        return signUpResponse;
    }

    @Override
    public ServiceProvider onboardProvider(ProviderOnboardingRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = repositoryFactory.getUserRepository().findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == null || !user.getRole().getName().equals("PROVIDER")) {
            throw new RuntimeException("Error: Only users with ROLE_PROVIDER can onboard.");
        }

        if (repositoryFactory.getServiceProviderRepository().existsByUserId(user.getId())) {
            throw new RuntimeException("Error: User has already onboarded.");
        }

        // --- UPDATED: Fetch multiple categories ---
        List<ServiceCategory> categories = repositoryFactory.getServiceCategoryRepository().findAllById(request.getCategoryIds());

        // Ensure all provided IDs were valid and found in the DB
        if (categories.isEmpty() || categories.size() != request.getCategoryIds().size()) {
            throw new RuntimeException("Error: One or more Service Categories are invalid or do not exist.");
        }

        ServiceProvider providerProfile = new ServiceProvider();
        providerProfile.setUser(user);

        // Set the collection of categories
        providerProfile.setServiceCategories(new HashSet<>(categories));

        providerProfile.setExperienceYears(request.getExperienceYears());
        providerProfile.setHourlyRate(request.getHourlyRate());
        providerProfile.setBio(request.getBio());
        providerProfile.setStatus("VERIFICATION PENDING");
        // Map and add schedules
        for (ScheduleRequest scheduleReq : request.getSchedules()) {
            if (scheduleReq.getEndTime().isBefore(scheduleReq.getStartTime())) {
                throw new RuntimeException("Error: End time cannot be before Start time on " + scheduleReq.getDayOfWeek());
            }

            Schedule newSchedule = new Schedule();
            newSchedule.setDayOfWeek(scheduleReq.getDayOfWeek());
            newSchedule.setStartTime(scheduleReq.getStartTime());
            newSchedule.setEndTime(scheduleReq.getEndTime());

            providerProfile.addSchedule(newSchedule);
        }

        ServiceProvider savedProvider = repositoryFactory.getServiceProviderRepository().save(providerProfile);
        // Extract category names for the email
        String categoryNames = savedProvider.getServiceCategories().stream()
                .map(ServiceCategory::getName)
                .reduce((a, b) -> a + ", " + b).orElse("Unknown");

        // Trigger the email to the Admin (run asynchronously in production, but fine synchronously for now)
        emailService.sendProviderVerificationEmail(
                savedProvider.getId(),
                savedProvider.getUser().getFirstName() + " " + savedProvider.getUser().getLastName(),
                categoryNames
        );

        return savedProvider;
    }
}
