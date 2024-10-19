package com.example.car_management.pagination.response;

import com.example.car_management.dto.TripDTO;
import com.example.car_management.pagination.request.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TripPageResponse extends Page {
    private List<TripDTO> trips;

    // Constructor phù hợp với các tham số cần thiết
    public TripPageResponse(Integer limit, Integer offset, List<TripDTO> trips) {
        super(limit, offset);
        this.trips = trips;
    }

    // Constructor phù hợp với các tham số cần thiết
    public TripPageResponse(Integer limit, Integer offset) {
        super(limit, offset);
    }
}
