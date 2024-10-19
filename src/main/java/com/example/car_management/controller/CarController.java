package com.example.car_management.controller;

import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.CarDTO;
import com.example.car_management.entity.Car;
import com.example.car_management.pagination.response.CarPageResponse;
import com.example.car_management.pagination.response.CarTypePageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.service.implement.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car")
@Tag(name = "Car Controller", description = "Quản lý thông tin xe")
public class CarController extends AtomicController{
    @Autowired
    private final CarService carService;

    @GetMapping
    @Operation(summary = "Retrieve all cars", description = "Fetches a list of all available cars")
    public ResponseEntity<ApiResponse> findAllCars() {
        List<CarDTO> cars = carService.findAll();
        ApiResponse<List<CarDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(cars);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/page")
    @Operation(summary = "Retrieve paginated cars", description = "Fetches a paginated list of cars with sorting options")
    public ResponseEntity<?> findAllCars(
            @RequestParam(defaultValue = "5", required = false) final Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) final Integer pageNo,
            @RequestParam(defaultValue = "id", required = false) final String sortBy) {
        PageResponse<?> carPageResponse = carService.findAllPaginationWithSortBy(pageSize, pageNo, sortBy);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(carPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @GetMapping("/search")
//    public ResponseEntity<?> searchCars(
//            @RequestParam(required = false, defaultValue = "") String licensePlate,
//            @RequestParam(required = false) Integer numberOfSeats,
//            @RequestParam(required = false, defaultValue = "") String carTypeName,
//            @RequestParam(required = false, defaultValue = "Hoạt động") String status,
//            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
//            @RequestParam(defaultValue = "5", required = false) Integer pageSize) {
//
//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        Page<Car> carPage = carService.searchCars(licensePlate, carTypeName, numberOfSeats, status, pageable);
//        ApiResponse<Page<Car>> apiResponse = new ApiResponse<>();
//        apiResponse.setData(carPage);
//        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
//    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCars(
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) Integer numberOfSeats,
            @RequestParam(required = false) String carTypeName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(defaultValue = "5", required = false) Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        CarPageResponse carPage = carService.findAllPaginationWithSearch(pageSize, pageNo, licensePlate, carTypeName, numberOfSeats, status);
        ApiResponse<CarPageResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(carPage);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Retrieve car by ID", description = "Fetches the car details by its ID")
    public ResponseEntity<?> findCarTypeById(
            @PathVariable("id")
            @NotNull
            @Positive(message = "NOT_POSITIVE") final Integer id) {
        CarDTO carResponse = carService.findById(id);
        ApiResponse<CarDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping
    @Operation(summary = "Add a new car", description = "Inserts a new car into the system")
    public ResponseEntity<?> addCar(@RequestBody final CarDTO carRequest) {
        CarDTO carResponse = carService.insert(carRequest);
        ApiResponse<CarDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update car details", description = "Updates the details of an existing car by its ID")
    public ResponseEntity<?> updateCar(@PathVariable("id") final Integer id, @RequestBody final CarDTO carRequest) {
        CarDTO carResponse = carService.update(carRequest, id);
        ApiResponse<CarDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car", description = "Deletes an existing car by its ID")
    public ResponseEntity<?> deleteCar(@PathVariable("id") final Integer id) {
        CarDTO carResponse = carService.findById(id);
        carService.deleteById(id);
        ApiResponse<CarDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
