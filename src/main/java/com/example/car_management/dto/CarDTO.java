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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String STATUS = "Hoạt động";
    private static final String EMPTY = "";

    private Integer carId;

    @NotNull(message = "car type must not null")
    private CarTypeDTO carType;

    private String image = EMPTY;

    @NotNull(message = "seats must not null")
    @Positive(message = "seats must a positive number")
    @Min(value = 4)
    private Integer numberOfSeats;

    @NotNull(message = "licensePlate must not null")
    @NotEmpty(message = "licensePlate must not empty")
    private String licensePlate;

    @NotNull(message = "status must not null")
    @NotEmpty(message = "status must not empty")
    private String status = STATUS;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}