package com.db.database;

import com.db.database.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RepositoryFactory {
    private @Autowired UserRepository userRepository;
    private @Autowired RolesRepository rolesRepository;
    private @Autowired PermissionsRepository permissionsRepository;
    private @Autowired AddressRepository addressRepository;
    private @Autowired BookingRepository bookingRepository;
    private @Autowired PaymentRepository paymentRepository;
    private @Autowired ReviewsRepository reviewsRepository;
    private @Autowired ScheduleRepository scheduleRepository;
    private @Autowired ServiceProviderRepository serviceProviderRepository;
    private @Autowired ServiceCategoryRepository serviceCategoryRepository;
}
