package com.example.car_management.controller;

import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.response.DepartureResponse;
import com.example.car_management.dto.response.DestiantionResponse;
import com.example.car_management.dto.TripDTO;
import com.example.car_management.pagination.response.CarPageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.pagination.response.TripPageResponse;
import com.example.car_management.service.implement.TripService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip")
public class TripController extends AtomicController{
    @Autowired
    private final TripService tripService;

    @Autowired
    private final AtomicLong atomicLong;

    @GetMapping
    public ResponseEntity<?> findAllTrips() {
        List<TripDTO> trips = tripService.findAll();
        ApiResponse<List<TripDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(trips);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/all-trips")
    public ResponseEntity<?> findAllTrip() {
        List<String> trips = tripService.findAllTrips();
        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setData(trips);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/page")
    public ResponseEntity<?> findAllCars(
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "id") String sortBy) {
        PageResponse<?> tripPageResponse = tripService.findAllPaginationWithSortBy(pageSize, pageNo, sortBy);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findTripById(
            @PathVariable("id")
            @NotNull
            @Positive(message = "NOT_POSITIVE") final Integer id) {
        TripDTO tripResponse = tripService.findById(id);
        ApiResponse<TripDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping()
    public ResponseEntity<?> addTrip(@RequestBody @Valid final TripDTO tripRequest) {
        TripDTO tripResponse = tripService.insert(tripRequest);
        ApiResponse<TripDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateTrip(@PathVariable("id") final Integer id, @RequestBody @Valid final TripDTO tripRequest) {
        TripDTO tripResponse = tripService.update(tripRequest, id);
        ApiResponse<TripDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable("id") final Integer id) {
        TripDTO tripResponse = tripService.findById(id);
        tripService.deleteById(id);
        ApiResponse<TripDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/departure")
    public ResponseEntity<?> getAllDeparture() {
        List<DepartureResponse> departures = tripService.getAllDeparture();
        ApiResponse<List<DepartureResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(departures);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/destination")
    public ResponseEntity<?> getAllDestiantion() {
        List<DestiantionResponse> destinations = tripService.getAllDestination();
        ApiResponse<List<DestiantionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(destinations);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTrips(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(defaultValue = "5", required = false) Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        TripPageResponse tripPageResponse = tripService.findAllPaginationWithSearch(pageSize, pageNo, departure, destination);
        ApiResponse<TripPageResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(tripPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

}