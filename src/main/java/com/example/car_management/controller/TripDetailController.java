package com.example.car_management.controller;

import com.example.car_management.dto.FilteredTripDetailDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.TripDetailDTO;
import com.example.car_management.pagination.response.CarPageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.pagination.response.TripDetailPageResponse;
import com.example.car_management.service.SearchTripService;
import com.example.car_management.service.implement.TripDetailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip-detail")
public class TripDetailController extends AtomicController{
    @Autowired
    private final TripDetailService tripDetailService;

    @Autowired
    private final SearchTripService searchTripService;

    @GetMapping
    public ResponseEntity<?> findAllTripDetails() {
        List<TripDetailDTO> tripDetails = tripDetailService.findAll();
        ApiResponse<List<TripDetailDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripDetails);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/page")
    public ResponseEntity<?> findAllCars(
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "id") String sortBy) {
        PageResponse<?> tripDetailPageResponse = tripDetailService.findAllPaginationWithSortBy(pageSize, pageNo, sortBy);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripDetailPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findTripDetailById(
            @PathVariable("id")
            @NotNull
            @Positive(message = "NOT_POSITIVE") final Integer id) {
        TripDetailDTO tripDetailDTO = tripDetailService.findById(id);
        ApiResponse<TripDetailDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripDetailDTO);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping()
    public ResponseEntity<?> addTripDetail(@RequestBody @Valid final TripDetailDTO tripDetailRequest) {
        TripDetailDTO tripDetailResponse = tripDetailService.insert(tripDetailRequest);
        ApiResponse<TripDetailDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripDetailResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateTripDetail(@PathVariable("id") final Integer id, @RequestBody @Valid final TripDetailDTO tripDetailRequest) {
        TripDetailDTO tripDetailResponse = tripDetailService.update(tripDetailRequest, id);
        ApiResponse<TripDetailDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripDetailResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteTripDetail(@PathVariable("id") final Integer id) {
        TripDetailDTO tripDetailResponse = tripDetailService.findById(id);
        tripDetailService.deleteById(id);
        ApiResponse<TripDetailDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripDetailResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<?> searchTripDetails(
            @RequestParam("departure") final String departure,
            @RequestParam("destination") final String destination,
            @RequestParam("departure-date") final LocalDate departureDate,
            @RequestParam("car-type") final String carTypeName,
            @RequestParam("min-price") final Long minPrice,
            @RequestParam("max-price") final Long maxPrice,
            @RequestParam("start-time") final LocalTime startTime,
            @RequestParam("end-time") final LocalTime endtTime) {


        // Gọi service để tìm kiếm dữ liệu
        List<FilteredTripDetailDTO> filteredTripDetails = searchTripService.findTripDetailsByCondition(
                departure, destination, departureDate, carTypeName, minPrice, maxPrice, startTime, endtTime);

        // Tạo ApiResponse để trả về dữ liệu
        ApiResponse<List<FilteredTripDetailDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(filteredTripDetails);

        // Trả về dữ liệu với mã trạng thái HTTP 200
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTripDetails(
            @RequestParam("departure") final String departure,
            @RequestParam("destination") final String destination,
            @RequestParam("departure-date") final LocalDate departureDate) {

        // Gọi service để tìm kiếm dữ liệu
        List<FilteredTripDetailDTO> filteredTripDetails = searchTripService.findTripDetailsByCondition(
                departure, destination, departureDate);

        // Tạo ApiResponse để trả về dữ liệu
        ApiResponse<List<FilteredTripDetailDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(filteredTripDetails);

        // Trả về dữ liệu với mã trạng thái HTTP 200
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search/page")
    public ResponseEntity<?> searchTripDetails(
            @RequestParam("departure") final String departure,
            @RequestParam("destination") final String destination,
            @RequestParam("departure-date") final LocalDate departureDate,
            @RequestParam(value = "page", defaultValue = "0") int pageNumber) { // Thêm Pageable vào tham số


        // Đặt kích thước trang là 3
        Pageable pageable = PageRequest.of(pageNumber, 3); // Mỗi trang chỉ hiển thị 3 record

        // Gọi service để tìm kiếm dữ liệu với phân trang
        Page<?> filteredTripDetails = searchTripService.findTripDetailsByCondition(
                departure, destination, departureDate, pageable);

        // Tạo ApiResponse để trả về dữ liệu
        ApiResponse<Page<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(filteredTripDetails); // Lấy danh sách dữ liệu trong trang

        // Trả về dữ liệu với mã trạng thái HTTP 200
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/searchTripDetails")
    public ResponseEntity<?> searchTripDetails(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) String carTypeName,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(defaultValue = "5", required = false) Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        TripDetailPageResponse tripPage = tripDetailService.findAllPaginationWithSearch(
                pageSize, pageNo, departure, destination, licensePlate, carTypeName, minPrice, maxPrice, startTime, endTime);

        ApiResponse<TripDetailPageResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripPage);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


}