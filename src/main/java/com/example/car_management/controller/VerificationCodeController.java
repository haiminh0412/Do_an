package com.example.car_management.controller;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.VerificationCodeResponse;
import com.example.car_management.service.implement.VerificationCodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification-code")
@AllArgsConstructor
public class  VerificationCodeController {
    private final VerificationCodeService verificationCodeService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse> confirmCancell(@PathVariable("bookingId") final Integer bookingId) {
        VerificationCodeResponse verificationCode = verificationCodeService.confirmCancellation(bookingId);
        ApiResponse<VerificationCodeResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(verificationCode);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{bookingId}/{cancelCode}")
    public ResponseEntity<ApiResponse>cancelTicket(
            @PathVariable("bookingId") final Integer bookingId,
            @PathVariable("cancelCode") final String cancelCode
            ) {
        BookingDTO cancelBooking = verificationCodeService.cancelBooking(bookingId, cancelCode);
        ApiResponse<BookingDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(cancelBooking);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
