package com.example.car_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionDTO implements Serializable {

    private Long id;

    @NotNull(message = "NULL")
    @NotNull(message = "EMPTY")
    private String code;

    @NotNull(message = "NULL")
    @NotNull(message = "EMPTY")
    private String description;

    @Positive(message = "NOT_POSITIVE")
    private double discountPercentage;

    private LocalDate startDate;

    private LocalDate endDate;

    @Positive(message = "NOT_POSITIVE")
    private Integer usageLimit = 200;

    public String getCode() {
        return code.toUpperCase().trim();
    }
}