package com.example.car_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class TripDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tripDetailId;

    @NotNull(message = "NULL")
    private TripDTO trip;

    @NotNull(message = "NULL")
    private CarDTO car;

    @NotNull(message = "NULL")
    @Min(value = 0, message = "INVALID_PRICE")
    private Long price;

    @NotNull(message = "NULL")
    private LocalTime departureTime;

    @NotNull(message = "NULL")
    private LocalTime destinationTime;
}
