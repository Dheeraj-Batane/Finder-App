package com.db.payments;

public interface PaymentService {
    public PaymentResponse createPaymentIntent(PaymentRequest request);
}
