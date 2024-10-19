package com.example.car_management.controller;

import com.example.car_management.dto.CustomerDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.service.implement.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@Tag(name = "Customer Controller", description = "Quản lý thông tin khách hàng")
public class CustomerController extends AtomicController{
    @Autowired
    CustomerService customerService;

    @GetMapping
    @Operation(summary = "Retrieve all customers", description = "Fetches a list of all customers")
    public ResponseEntity<?> findAllCustomer() {
        List<CustomerDTO> customers = customerService.findAll();
        ApiResponse<List<CustomerDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(customers);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping()
    @Operation(summary = "Add a new customer", description = "Inserts a new customer into the system")
    public ResponseEntity<?> addCustomer(@RequestBody @Valid CustomerDTO customerRequest) {
        CustomerDTO carTypeResponse = customerService.insert(customerRequest);
        ApiResponse<CustomerDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypeResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

//    @PutMapping("/update-customer")
//    public ResponseEntity<?> updateAccount(@RequestBody  Customer customer) {
//        customerService.updateCustomer(customer);
//        return ResponseEntity.ok(customer);
//    }
//
//    @DeleteMapping("/delete-customer")
//    public ResponseEntity<?> deleteAccount(@RequestBody  Customer customer) {
//        customerService.deleteCustomer(customer);
//        return ResponseEntity.ok(customer);
//    }
}
