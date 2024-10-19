package com.example.car_management.dto.request;

import com.example.car_management.dto.response.CarResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    //private Integer seatId;

    @NotNull(message = "car must not null")
    private CarResponse car;

    @NotNull(message = "seat number must not null")
    @NotEmpty(message = "seat number must not empty")
    private String seatNumber;
}
