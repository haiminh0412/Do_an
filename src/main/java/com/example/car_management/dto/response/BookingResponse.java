package com.example.car_management.dto.response;

import com.example.car_management.dto.request.BookingRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {
    private int bookingId;
    private BookingRequest bookingRequest;
    private LocalDateTime bookingAt;
}
