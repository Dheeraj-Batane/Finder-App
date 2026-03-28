package com.db.servicecategory;

import com.db.database.entities.Schedule;
import com.db.database.entities.ServiceCategory;
import com.db.database.entities.User;
import jakarta.persistence.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
