package com.db.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {
    private Long bookingId;
    private String providerName;
    private String categoryName;
    private String appointmentDate;
    private String appointmentTime;
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
    private Integer reviewStars;
    private String reviewComments;
}
