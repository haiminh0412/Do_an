package com.example.car_management.pagination.response;

import com.example.car_management.dto.TripDetailDTO;
import com.example.car_management.pagination.request.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TripDetailPageResponse extends Page {
    private List<TripDetailDTO> tripDetails;

    // Constructor phù hợp với các tham số cần thiết
    public TripDetailPageResponse(Integer limit, Integer offset, List<TripDetailDTO> tripDetails) {
        super(limit, offset);
        this.tripDetails = tripDetails;
    }

    // Constructor phù hợp với các tham số cần thiết
    public TripDetailPageResponse(Integer limit, Integer offset) {
        super(limit, offset);
    }
}
