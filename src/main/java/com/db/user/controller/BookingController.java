package com.db.user.controller;

import com.db.common.Response;
import com.db.database.entities.Booking;
import com.db.database.entities.Payment;
import com.db.user.dto.BookingListResponse;
import com.db.user.dto.BookingRequest;
import com.db.user.dto.PaymentRequest;
import com.db.user.dto.ReviewRequest;
import com.db.user.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    BookingService bookingService;
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        try {
            Booking savedBooking = bookingService.createBooking(request);
            // In a real app, you might want to return a BookingResponse DTO instead of the entity directly
            return new ResponseEntity<>("Booking created successfully with ID: " + savedBooking.getId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping(value = "/payment")
    public ResponseEntity<?> savePayment(@Valid @RequestBody PaymentRequest request) {
        try {
            Payment savedPayment = bookingService.processPayment(request);
            return new ResponseEntity<>("Payment processed successfully. Transaction ID: " + savedPayment.getTxNumber(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    // ... inside BookingController.java ...
    @GetMapping("/confirm/{id}")
    public ResponseEntity<String> confirmBooking(@PathVariable Long id) {
        try {
            bookingService.confirmBooking(id);

            // Return a simple HTML success page
            String htmlResponse = "<html><body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>"
                    + "<h1 style='color: #4CAF50;'>Appointment Confirmed!</h1>"
                    + "<p>You have successfully accepted the booking. The customer is waiting for you.</p>"
                    + "</body></html>";
            return ResponseEntity.ok().body(htmlResponse);

        } catch (RuntimeException e) {
            // Return a simple HTML error page if it was already confirmed or not found
            String errorHtml = "<html><body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>"
                    + "<h1 style='color: #f44336;'>Confirmation Failed</h1>"
                    + "<p>" + e.getMessage() + "</p>"
                    + "</body></html>";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorHtml);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BookingListResponse> getUserBookings(@PathVariable Long userId) {
        BookingListResponse response = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/review")
    public ResponseEntity<Response> addReview(@Valid @RequestBody ReviewRequest request) {
        try {
            Response response = bookingService.addReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Response errorResponse = new Response();
            errorResponse.setResponseCode("99999999");
            errorResponse.setResponseMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
