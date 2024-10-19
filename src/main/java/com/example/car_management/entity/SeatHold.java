package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "SeatHold")
public class SeatHold {

    public static final String TABLE_NAME = "SeatHold";
    public static final String SEAT_HOLD_ID = "id";
    public static final String SESSION_ID = "sessionId";
    public static final String HOLD_START = "holdStart";
    public static final String SEAT = "seat";
    public static final String TRIP_DETAIL = "tripDetail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer seatHoldId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "hold_start", nullable = false)
    private LocalDateTime holdStart;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Seat_ID", nullable = false, referencedColumnName = "Seat_ID")
    @JsonBackReference
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Trip_Detail_ID", nullable = false, referencedColumnName = "Trip_Detail_ID")
    @JsonBackReference
    private TripDetail tripDetail;
}
