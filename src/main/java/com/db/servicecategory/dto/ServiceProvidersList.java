package com.db.servicecategory.dto;

import com.db.database.entities.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServiceProvidersList {
    private Long providerId;
    private String firstName;
    private String lastName;
    private Double hourlyRate;
    private Integer experienceYears;
    private String bio;
    private String status;
    private List<Schedule> schedules = new ArrayList<>();
    private List<ProviderReviewResponse> reviews;
}
