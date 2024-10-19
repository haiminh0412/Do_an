package com.example.car_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportBookingDTO {
    private String customerName;
    private String trip;
    private Integer ticketCount;
    LocalDate departureDate;
    LocalDateTime bookingDate;
    Integer totalAmount;
    String status;
}
