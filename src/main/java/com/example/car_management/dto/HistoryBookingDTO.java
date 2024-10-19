package com.example.car_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryBookingDTO {
    private Integer historyId;
    private BookingDTO booking;
    private Integer seatCount;
    private Long totalPrice;
}
