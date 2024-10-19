package com.example.car_management.controller;

import com.example.car_management.dto.request.BookingRequest;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.response.BookingSeatResponse;
import com.example.car_management.service.implement.BookingSeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking-seat")
@Tag(name = "Booking Seat Controller", description = "Quản lý các thao tác đặt ghế")
public class BookingSeatController {
    @Autowired
    BookingSeatService bookingSeatService;

    @PostMapping("/{promotionCode}")
    @Operation(summary = "Book a seat with promotion code", description = "Allows booking a seat using a promotion code and sends a confirmation email")
    public ResponseEntity<?> bookingWithPromotionCode(@RequestBody final BookingRequest bookingRequest,
                                                      @PathVariable("promotionCode") final String promotionCode) {
        BookingSeatResponse bookingSeatResponse = bookingSeatService.bookingWithPromotionCode(bookingRequest, promotionCode);
        bookingSeatService.sendEmail(bookingSeatResponse);
        ApiResponse<BookingSeatResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookingSeatResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping
    @Operation(summary = "Book a seat", description = "Allows booking a seat and sends a confirmation email")
    public ResponseEntity<?> booking(@RequestBody final BookingRequest bookingRequest) {
        BookingSeatResponse bookingSeatResponse = bookingSeatService.booking(bookingRequest);
        bookingSeatService.sendEmail(bookingSeatResponse);
        ApiResponse<BookingSeatResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookingSeatResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
