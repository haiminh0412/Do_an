package com.example.car_management.controller;

import com.example.car_management.dto.ReportBookingDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.pagination.response.CarTypePageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<?> findAllCarTypes(
            @RequestParam(defaultValue = "2", required = false) final Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) final Integer pageNo,
            @RequestParam(defaultValue = "id", required = false) final String... sorts) {
        PageResponse<?> bookingPageResponse = reportService.findAllPaginationWithSortByMultipleColumns(pageSize, pageNo, sorts);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookingPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBookings(
            @RequestParam(defaultValue = "2", required = false) final Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) final Integer pageNo,
            @RequestParam(required = false) final String customerName,
            @RequestParam(required = false) final String trip,
            @RequestParam(required = false) final String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate) {

        PageResponse<?> bookingPageResponse = reportService.searchBookings(pageSize, pageNo, customerName, trip, startDate, endDate, status);

        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookingPageResponse);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/exportBookings")
    public ResponseEntity<byte[]> exportBookings(@RequestBody List<ReportBookingDTO> bookings) throws IOException {
        // Tạo file Excel
        ByteArrayOutputStream outputStream = reportService.exportBookingsToExcel(bookings);

        // Đặt tên file
        String fileName = "Bookings.xlsx";

        // Trả về file Excel dưới dạng byte[]
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(outputStream.toByteArray());
    }
}
