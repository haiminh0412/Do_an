package com.example.car_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer seatId;

    private CarDTO car;

    @NotNull(message = "seat number must not null")
    @NotEmpty(message = "seat number must not empty")
    private String seatNumber;

    private Integer x;
    private Integer y;
}