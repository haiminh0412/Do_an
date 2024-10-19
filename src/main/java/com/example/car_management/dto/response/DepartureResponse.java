package com.example.car_management.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartureResponse {
    @NotNull(message = "departure must not null")
    @NotEmpty(message = "departure must no empty")
    private String departure;
}
