package com.db.payments;

import com.db.common.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse extends Response {
    private String clientSecret;
}