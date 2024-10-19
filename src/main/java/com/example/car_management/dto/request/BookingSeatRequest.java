package com.example.car_management.dto.request;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.SeatDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingSeatRequest {
    private BookingDTO booking;
    private SeatDTO seat;
}
