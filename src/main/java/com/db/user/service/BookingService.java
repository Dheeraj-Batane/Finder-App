package com.db.user.service;

import com.db.database.entities.Booking;
import com.db.database.entities.Payment;
import com.db.user.dto.BookingListResponse;
import com.db.user.dto.BookingRequest;
import com.db.user.dto.PaymentRequest;

public interface BookingService {
    public Booking createBooking(BookingRequest request);
    public Payment processPayment(PaymentRequest request);
    public void confirmBooking(Long bookingId);
    public BookingListResponse getUserBookings(Long userId);
}
