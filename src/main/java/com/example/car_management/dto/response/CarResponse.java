package com.example.car_management.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer carId;

    private CarTypeResponse carType;

    private String image;

    private Integer seats;

    private String licensePlate;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}