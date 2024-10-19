package com.example.car_management.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("SeatStatus")
public class SeatStatus {
    private int seatId;
    private int tripDetailId;
    private String status;

}
