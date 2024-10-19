package com.example.car_management.controller;

import com.example.car_management.dto.HistoryBookingDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.service.implement.HistoryBookingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history-booking")
public class HistoryBookingController {
    private final HistoryBookingsService historyBookingsService;

    public ResponseEntity<ApiResponse> findAllHistoryBooking() {
        List<HistoryBookingDTO> historyBookings = historyBookingsService.getAllHistoryBookings();
        ApiResponse<List<HistoryBookingDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(historyBookings);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
