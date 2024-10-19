package com.example.car_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryBookingResponse implements Serializable {
    private Integer bookingId;
    private String startDestination;
    private String endDestination;
    private LocalDate departureDate;
    private LocalDateTime bookingAt;
    private String status;
    private String departure;
    private String destination;
    private Long price;
    private LocalTime departureTime;
    private LocalTime destinationTime;
    private String carType;
    private String licensePlate;
    private String[] bookedSeats;
    private int numberSeats;
    private Long totalPrice;
}
