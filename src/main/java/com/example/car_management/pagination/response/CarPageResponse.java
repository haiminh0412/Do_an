package com.example.car_management.pagination.response;

import com.example.car_management.dto.CarDTO;
import com.example.car_management.pagination.request.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CarPageResponse extends Page {
    private List<CarDTO> cars;

    // Constructor phù hợp với các tham số cần thiết
    public CarPageResponse(Integer limit, Integer offset, List<CarDTO> cars) {
        super(limit, offset);
        this.cars = cars;
    }

    // Constructor phù hợp với các tham số cần thiết
    public CarPageResponse(Integer limit, Integer offset) {
        super(limit, offset);
    }
}
