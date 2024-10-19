package com.example.car_management.dto.request;

import com.example.car_management.dto.TripDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchTripRequest {
    private String departure;
    private String destination;
    private Date departureDate;
    private LocalTime departureTime;
    private String carType;
    private Long minPrice;
    private Long maxPrice;
    private TripDetailDTO tripDetail;
}
