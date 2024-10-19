package com.example.car_management.dto.request;

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
public class CustomerRequest {
    @NotNull(message = "name must not null")
    @NotEmpty(message = "name must not empty")
    private String name;

    @NotNull(message = "phone must not null")
    @NotEmpty(message = "phone must not empty")
    private String phone;

    @NotNull(message = "email must not null")
    @NotEmpty(message = "email must not empty")
    private String email;
}
