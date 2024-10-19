package com.example.car_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class FilteredTripDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    TripDetailDTO tripDetail;
    Integer availableSeats;
}
