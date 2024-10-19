package com.example.car_management.dto.request;

import com.example.car_management.dto.CustomerDTO;
import com.example.car_management.dto.TripDetailDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequest {
    private CustomerDTO customer;
    private TripDetailDTO tripDetail;
    private String startDestination;
    private String endDestination;
    private LocalDate departureDate;
    private List<Integer> seatsIdSelected;
}
