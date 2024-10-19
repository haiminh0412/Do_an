package com.example.car_management.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingInfoDTO {
    private Integer customerId;
    private String customerName;
    private String phone;
    private String email;
    private Integer bookingId;
    private String startDestination;
    private String endDestination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime destinationTime;
    private String tripDeparture;
    private String tripDestination;

}
