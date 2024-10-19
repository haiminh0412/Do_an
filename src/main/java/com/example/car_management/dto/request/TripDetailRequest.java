package com.example.car_management.dto.request;

import com.example.car_management.dto.response.CarResponse;
import com.example.car_management.dto.response.TripResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDetailRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    //private Integer tripDetailId;

    @NotNull(message = "trip must not null")
    private TripResponse trip;

    @NotNull(message = "car must not null")
    private CarResponse car;

    @NotNull(message = "price must not null")
    @Positive(message = "price must a positive number")
    @Min(value = 0)
    private Long price;

    @NotNull(message = "departureTime must not null")
    private LocalTime departureTime;

    @NotNull(message = "destinationTime must not null")
    private LocalTime destinationTime;
}
