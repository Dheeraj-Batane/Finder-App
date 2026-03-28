package com.db.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProviderOnboardingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "At least one Service Category ID must be selected")
    private Set<Long> categoryIds;

    @NotNull(message = "Experience years cannot be null")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly rate must be greater than zero")
    private Double hourlyRate;

    @NotBlank(message = "Bio is required")
    private String bio;

    // --- NEW ADDITION ---
    @Valid
    @NotEmpty(message = "At least one working schedule must be provided")
    private List<ScheduleRequest> schedules;
}
