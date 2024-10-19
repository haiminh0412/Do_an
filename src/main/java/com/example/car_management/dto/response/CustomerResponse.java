package com.example.car_management.dto.response;

import com.example.car_management.dto.request.CustomerRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private Integer customerId;
    private CustomerRequest customerRequest;
}
