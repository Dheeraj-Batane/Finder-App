package com.db.auth.dto;
import lombok.*;

@Getter
@Setter
public class AddressRequest {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String addressType;
}