package com.example.car_management.controller;

import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.CarTypeDTO;
import com.example.car_management.pagination.response.CarTypePageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.service.implement.CarTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car-type")
@Tag(name = "Car Type Controller", description = "Quản lý thông tin loại xe")
public class CarTypeController {
    @Autowired
    private final CarTypeService carTypeService;

    @GetMapping
    @Operation(summary = "Retrieve all car types", description = "Fetches a list of all available car types")
    public ResponseEntity<?> findAllCarTypes() {
        List<CarTypeDTO> carTypes = carTypeService.findAll();
        ApiResponse<List<CarTypeDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypes);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/page")
    @Operation(summary = "Retrieve paginated car types", description = "Fetches a paginated list of car types with sorting options")
    public ResponseEntity<?> findAllCarTypes(
            @RequestParam(defaultValue = "5", required = false) final Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) final Integer pageNo,
            @RequestParam(defaultValue = "id", required = false) final String sortBy) {
        PageResponse<?> carTypePageResponse = carTypeService.findAllPaginationWithSortBy(pageSize, pageNo, sortBy);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypePageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/page/multiple/")
    @Operation(summary = "Retrieve paginated car types with multiple sorting", description = "Fetches a paginated list of car types sorted by multiple columns")
    public ResponseEntity<?> findAllCarTypes(
            @RequestParam(defaultValue = "5", required = false) final Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) final Integer pageNo,
            @RequestParam(defaultValue = "id", required = false) final String... sorts) {
        PageResponse<?> carTypePageResponse = carTypeService.findAllPaginationWithSortByMultipleColumns(pageSize, pageNo, sorts);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypePageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search/{name}")
    @Operation(summary = "Search car types by name", description = "Fetches a paginated list of car types matching the given name")
    public ResponseEntity<?> findCarTypeByNameLike(
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "id") String sortBy,
            @PathVariable(value = "name", required = false) String name)
    {
        CarTypePageResponse carTypePageResponse;
        System.out.println(name.toLowerCase().trim());
        carTypePageResponse = carTypeService.findAllPaginationWithSearch(pageSize, pageNo, name.toLowerCase().trim());
        ApiResponse<CarTypePageResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypePageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve car type by ID", description = "Fetches the car type details by its ID")
    public ResponseEntity<?> findCarTypeById(
            @PathVariable("id")
            @NotNull
            @Positive(message = "NOT_POSITIVE") final Integer id) {

        CarTypeDTO carTypeResponse = carTypeService.findById(id);
        ApiResponse<CarTypeDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypeResponse);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping
    @Operation(summary = "Add a new car type", description = "Inserts a new car type into the system")
    public ResponseEntity<?> addCarType(@RequestBody @Valid final CarTypeDTO carTypeRequest) {
        CarTypeDTO carTypeResponse = carTypeService.insert(carTypeRequest);
        ApiResponse<CarTypeDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypeResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update car type details", description = "Updates the details of an existing car type by its ID")
    public ResponseEntity<?> updateCarType(@PathVariable("id") final Integer id,
                                           @RequestBody @Valid final CarTypeDTO carTypeRequest) {
        CarTypeDTO carTypeResponse = carTypeService.update(carTypeRequest, id);
        ApiResponse<CarTypeDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypeResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete car type", description = "Deletes an existing car type by its ID")
    public ResponseEntity<?> deleteCarType(@PathVariable("id") final Integer id) {
        CarTypeDTO carTypeResponse = carTypeService.findById(id);
        carTypeService.deleteById(id);
        ApiResponse<CarTypeDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(carTypeResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}