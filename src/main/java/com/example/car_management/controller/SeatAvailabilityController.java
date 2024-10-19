package com.example.car_management.controller;

import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.SeatAvailability;
import com.example.car_management.service.SeatAvailabilitieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/seat-availability")
@Tag(name = "Seat Availability Controller", description = "Quản lý thông tin sẵn có của ghế")
public class SeatAvailabilityController {
    @Autowired
    SeatAvailabilitieService seatAvailabilitieService;

    @GetMapping("/{tripDetailId}/{departureDate}")
    @Operation(summary = "Get seat maps", description = "Fetches seat availability maps for a specific trip and departure date")
    public ResponseEntity<?> getSeatMaps(@PathVariable("tripDetailId") Integer tripDetailId,
                                                @PathVariable("departureDate") LocalDate departureDate) {
        SeatAvailability[][] seatAvailabilities = seatAvailabilitieService.getSeatMaps(tripDetailId, departureDate);
        ApiResponse<SeatAvailability[][]> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatAvailabilities);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @GetMapping("/{tripDetailId}/{departureDate}")
//    public ResponseEntity<?> getAllSeatAvailability(@PathVariable("tripDetailId") Integer tripDetailId,
//                                         @PathVariable("departureDate") LocalDate departureDate) {
//        List<SeatAvailability> seatAvailabilities = seatAvailabilitieService.getAvailableSeats(tripDetailId, departureDate);
//        ApiResponse<List<SeatAvailability>> apiResponse = new ApiResponse<>();
//        apiResponse.setData(seatAvailabilities);
//        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
//    }
}
