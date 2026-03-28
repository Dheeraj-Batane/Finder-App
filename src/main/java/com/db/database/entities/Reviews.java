package com.db.database.entities;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false) // Links directly to the completed booking
    private Booking booking;

    private Integer stars;

    @Column(length = 1000)
    private String comments;

    private String photos; // Can be a comma-separated string of image URLs

    @CreationTimestamp
    private LocalDateTime createDate;
}
