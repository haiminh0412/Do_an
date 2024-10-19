package com.example.car_management.pagination.response;

import com.example.car_management.dto.CarTypeDTO;
import com.example.car_management.pagination.request.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CarTypePageResponse extends Page {
    private List<CarTypeDTO> carTypes;

    // Constructor phù hợp với các tham số cần thiết
    public CarTypePageResponse(Integer limit, Integer offset, List<CarTypeDTO> carTypes) {
        super(limit, offset);
        this.carTypes = carTypes;
    }

    // Constructor phù hợp với các tham số cần thiết
    public CarTypePageResponse(Integer limit, Integer offset) {
        super(limit, offset);
    }
}
