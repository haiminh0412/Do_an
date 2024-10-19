package com.example.car_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatHoldDTO implements Serializable {
    private Integer seatHoldId;
    private Integer seatId;
    private Integer tripDetailId;
    private String sessionId;
    private LocalDate departureDate;
    private LocalDateTime holdStart;
    private int status;
}

