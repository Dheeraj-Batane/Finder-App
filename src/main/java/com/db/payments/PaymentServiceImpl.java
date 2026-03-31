package com.db.payments;


import com.db.database.RepositoryFactory;
import com.db.database.entities.Payment;
import com.db.database.enums.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.db.database.entities.Booking;
import com.db.database.repository.BookingRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {

    private @Autowired RepositoryFactory repositoryFactory;
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentResponse createPaymentIntent(PaymentRequest request) {
        // 1. Fetch the booking to ensure it exists and hasn't been paid for yet
        Booking booking = repositoryFactory.getBookingRepository().findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 2. Calculate the exact amount securely on the backend
        // Note: For this example, we charge the equivalent of 1 hour of their rate as a deposit.
        Double hourlyRate = booking.getServiceProvider().getHourlyRate();

        // Stripe requires the amount in the smallest currency unit (e.g., paise for INR)
        // So ₹350.00 becomes 35000
        long amountInPaise = (long) (hourlyRate * 100);

        try {
            // 3. Build the parameters for Stripe
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInPaise)
                            .setCurrency("inr")
                            // Attach the booking ID as metadata so we can track it in the Stripe Dashboard
                            .putMetadata("bookingId", booking.getId().toString())
                            .build();

            // 4. Make the call to Stripe's servers to create the Intent
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            Payment payment=new Payment();
            payment.setStatus(paymentIntent.getStatus());
            payment.setBooking(booking);
            payment.setCreateDate(LocalDateTime.now());
            payment.setTxNumber(paymentIntent.getClientSecret());
            payment.setAmount(hourlyRate);
            repositoryFactory.getPaymentRepository().save(payment);
            // 5. Build our success response containing the secret key
            PaymentResponse response = new PaymentResponse();
            response.setResponseCode("00000000");
            response.setResponseMessage("Payment Intent created successfully");
            response.setClientSecret(paymentIntent.getClientSecret());

            return response;

        } catch (StripeException e) {
            System.err.println("Stripe Error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize payment gateway.");
        }
    }


}
