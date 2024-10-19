package com.example.car_management.dto;

import com.example.car_management.Enum.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {
    private int bookingId;
    private TripDetailDTO tripDetail;
    private CustomerDTO customer;
    private String startDestination;
    private String endDestination;
    private LocalDate departureDate;
    private LocalDateTime bookingAt;
    private String status = BookingStatus.PENDING.getStatus();
}
