package com.db.user.service;

import com.db.common.Response;
import com.db.database.RepositoryFactory;
import com.db.database.entities.*;
import com.db.database.entities.User;
import com.db.database.enums.BookingStatus;
import com.db.integration.EmailService;
import com.db.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private @Autowired RepositoryFactory repositoryFactory;
    private @Autowired EmailService emailService;
    @Override
    public Booking createBooking(BookingRequest request) {

        // 1. Prevent booking in the past (Date is checked by DTO, this checks the exact time if booked for today)
        if (request.getAppointmentDate().isEqual(LocalDate.now()) && request.getAppointmentTime().isBefore(LocalTime.now())) {
            throw new RuntimeException("Error: Cannot book an appointment in the past.");
        }

        // 2. Fetch User & Provider
        User user = repositoryFactory.getUserRepository().findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ServiceProvider provider = repositoryFactory.getServiceProviderRepository().findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Service Provider not found"));

        ServiceCategory category = repositoryFactory.getServiceCategoryRepository().findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Service Category not found"));

        // 3. Verify the provider actually offers this category
        if (!provider.getServiceCategories().contains(category)) {
            throw new RuntimeException("Error: This provider does not offer services in this category.");
        }

        // 4. Validate Provider's Working Schedule
        DayOfWeek requestDay = request.getAppointmentDate().getDayOfWeek();
        boolean isWithinWorkingHours = false;

        for (Schedule schedule : provider.getSchedules()) {
            if (schedule.getDayOfWeek() == requestDay) {
                // Check if requested time is between start and end time
                if (!request.getAppointmentTime().isBefore(schedule.getStartTime()) &&
                        request.getAppointmentTime().isBefore(schedule.getEndTime())) {
                    isWithinWorkingHours = true;
                    break;
                }
            }
        }

        if (!isWithinWorkingHours) {
            throw new RuntimeException("Error: The provider does not work on this day or at this time.");
        }

        // 5. Prevent Double Booking
        boolean isAlreadyBooked = repositoryFactory.getBookingRepository().existsByServiceProviderIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                provider.getId(),
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                BookingStatus.CANCELLED // We don't care if there is a cancelled booking at this time
        );

        if (isAlreadyBooked) {
            throw new RuntimeException("Error: This time slot is already booked for this provider. Please select another time.");
        }

        // 6. Create and Save Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setServiceProvider(provider);
        booking.setServiceCategory(category);
        booking.setAppointmentDate(request.getAppointmentDate());
        booking.setAppointmentTime(request.getAppointmentTime());
        booking.setStatus(BookingStatus.PENDING); // Default status until provider confirms

        Booking savedBooking = repositoryFactory.getBookingRepository().save(booking);

        // 2. Extract details for the email
        String providerEmail = provider.getUser().getEmail();
        String providerName = provider.getUser().getFirstName() + " " + provider.getUser().getLastName();
        String customerName = user.getFirstName() + " " + user.getLastName();
        String categoryName = category.getName();
        String bookingDate = request.getAppointmentDate().toString();
        String bookingTime = request.getAppointmentTime().toString();

        // 3. Send the notification email to the provider
        emailService.sendBookingNotificationToProvider(
                providerEmail, providerName, customerName, categoryName,
                bookingDate, bookingTime, savedBooking.getId()
        );

        return savedBooking;
    }

    @Override
    public Payment processPayment(PaymentRequest request) {
        // 1. Validate Booking exists
        Booking booking = repositoryFactory.getBookingRepository().findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Error: Booking not found with ID: " + request.getBookingId()));

        // 2. Prevent duplicate transactions
        if (repositoryFactory.getPaymentRepository().existsByTxNumber(request.getTxNumber())) {
            throw new RuntimeException("Error: This transaction number has already been processed.");
        }

        // 3. Create and Save Payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTxNumber(request.getTxNumber());
        payment.setAmount(request.getAmount());
        payment.setStatus(request.getPaymentStatus().toUpperCase()); // Ensure uppercase (e.g., SUCCESS)

        Payment savedPayment = repositoryFactory.getPaymentRepository().save(payment);

        // 4. Update Booking Status if payment is successful
        if ("SUCCESS".equals(savedPayment.getStatus())) {
            booking.setStatus(BookingStatus.CONFIRMED);
            repositoryFactory.getBookingRepository().save(booking);
        } else if ("FAILED".equals(savedPayment.getStatus())) {
            // Optional: You can keep it PENDING or mark it CANCELLED based on your business logic
            booking.setStatus(BookingStatus.CANCELLED);
            repositoryFactory.getBookingRepository().save(booking);
        }

        return savedPayment;
    }

    @Transactional
    @Override
    public void confirmBooking(Long bookingId) {
        Booking booking = repositoryFactory.getBookingRepository().findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new RuntimeException("This booking has already been confirmed.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot confirm a cancelled booking.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        repositoryFactory.getBookingRepository().save(booking);

        String customerEmail = booking.getUser().getEmail();
        String customerName = booking.getUser().getFirstName() + " " + booking.getUser().getLastName();
        String providerName = booking.getServiceProvider().getUser().getFirstName() + " " + booking.getServiceProvider().getUser().getLastName();
        String categoryName = booking.getServiceCategory().getName();
        String bookingDate = booking.getAppointmentDate().toString();
        String bookingTime = booking.getAppointmentTime().toString();

        // 3. Send the confirmation email to the customer
        emailService.sendBookingConfirmationToCustomer(
                customerEmail, customerName, providerName, categoryName, bookingDate, bookingTime
        );
    }

    @Override
    public BookingListResponse getUserBookings(Long userId) {
        List<Booking> bookings = repositoryFactory.getBookingRepository().findByUserIdOrderByAppointmentDateDescAppointmentTimeDesc(userId);

        List<BookingResponse> bookingResponses = bookings.stream().map(booking -> {
            BookingResponse dto = new BookingResponse();
            dto.setBookingId(booking.getId());
            // Combine first and last name safely
            String providerName = booking.getServiceProvider().getUser().getFirstName() + " " +
                    booking.getServiceProvider().getUser().getLastName();
            dto.setProviderName(providerName);
            dto.setCategoryName(booking.getServiceCategory().getName());
            dto.setAppointmentDate(booking.getAppointmentDate().toString());

            // Format time to remove seconds (HH:mm)
            dto.setAppointmentTime(booking.getAppointmentTime().toString().substring(0, 5));
            dto.setStatus(booking.getStatus().name());

            repositoryFactory.getReviewsRepository().findByBookingId(booking.getId()).ifPresent(review -> {
                dto.setReviewStars(review.getStars());
                dto.setReviewComments(review.getComments());
            });
            return dto;
        }).toList();

        BookingListResponse response = new BookingListResponse();
        response.setResponseCode("00000000");
        response.setResponseMessage("Success");
        response.setData(bookingResponses);
        return response;
    }

    @Override
    @Transactional
    public Response addReview(ReviewRequest request) {
        // 1. Fetch the booking
        Booking booking = repositoryFactory.getBookingRepository().findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        // 2. Validate booking status (Must be completed to review)
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("You can only review completed services.");
        }

        // 3. Prevent duplicate reviews
        if (repositoryFactory.getReviewsRepository().existsByBookingId(booking.getId())) {
            throw new RuntimeException("You have already submitted a review for this booking.");
        }

        // 4. Save the review
        Reviews review = new Reviews();
        review.setBooking(booking);
        review.setStars(request.getStars());
        review.setComments(request.getComments());
        // For now, we leave photos null as handling file uploads requires AWS S3 or local file storage logic
        review.setPhotos(null);

        repositoryFactory.getReviewsRepository().save(review);

        Response response = new Response();
        response.setResponseCode("00000000");
        response.setResponseMessage("Review submitted successfully.");
        return response;
    }
}
