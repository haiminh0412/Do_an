package com.example.car_management.dto.response;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.SeatDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingSeatResponse {
    private BookingDTO booking;
    private List<SeatDTO> seats;
    private Long totalPrice;
}
