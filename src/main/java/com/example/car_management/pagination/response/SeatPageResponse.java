package com.example.car_management.pagination.response;

import com.example.car_management.dto.SeatDTO;
import com.example.car_management.pagination.request.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeatPageResponse extends Page {
    private List<SeatDTO> seats;

    // Constructor phù hợp với các tham số cần thiết
    public SeatPageResponse(Integer limit, Integer offset, List<SeatDTO> seats) {
        super(limit, offset);
        this.seats = seats;
    }

    // Constructor phù hợp với các tham số cần thiết
    public SeatPageResponse(Integer limit, Integer offset) {
        super(limit, offset);
    }
}
