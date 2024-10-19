package com.example.car_management.dto;

import com.example.car_management.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TripDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tripId;

    @NotNull(message = "NULL")
    @NotEmpty(message = "EMPTY")
    private String departure;

    @NotNull(message = "NULL")
    @NotEmpty(message = "EMPTY")
    private String destination;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String getDestination() {
        return StringUtils.normalizeString(this.destination);
    }

    public String getDeparture() {
        return StringUtils.normalizeString(this.departure);
    }
}