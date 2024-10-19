package com.example.car_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationCodeResponse {
    private Integer id;
    private String code;
    private BookingDTO booking;
    private String type;
    private LocalDateTime sendAt;
}
