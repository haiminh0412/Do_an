package com.example.car_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tripDetailId;

    private TripResponse trip;

    private CarResponse car;

    private Long price;

    private LocalTime departureTime;

    private LocalTime destinationTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}