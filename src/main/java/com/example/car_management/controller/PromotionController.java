package com.example.car_management.controller;

import com.example.car_management.dto.PromotionDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotions")
@Tag(name = "Promotion Controller", description = "Quản lý thông tin khuyến mãi")
public class PromotionController {
    @Autowired
    private final PromotionService promotionService;

    @PostMapping("/checkPromoCode/{code}/{originalPrice}")
    public ResponseEntity<ApiResponse> checkPromoCode(@PathVariable("code") String code,
                                                      @PathVariable("originalPrice") Long originalPrice) {
        Long totalPrice = promotionService.calculateTotalPriceWithPromotion(code, originalPrice);
        ApiResponse<Long> apiResponse = new ApiResponse<>();
        apiResponse.setData(totalPrice);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> findPromotions(@PathVariable("code") String code) {
        PromotionDTO promotion = promotionService.findByCode(code);
        ApiResponse<PromotionDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(promotion);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    
    // Get all promotions
    @GetMapping
    @Operation(summary = "Get all promotions", description = "Fetches a list of all promotions")
    public ResponseEntity<?> getAllPromotions() {
        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        ApiResponse<List<PromotionDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(promotions);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/page")
    @Operation(summary = "Retrieve paginated car types", description = "Fetches a paginated list of car types with sorting options")
    public ResponseEntity<?> findAllPromotions(
            @RequestParam(defaultValue = "5", required = false) final Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) final Integer pageNo,
            @RequestParam(defaultValue = "id", required = false) final String sortBy) {
        PageResponse<?> promotionPageResponse = promotionService.findAllPaginationWithSortBy(pageSize, pageNo, sortBy);
        ApiResponse<PageResponse<?>> apiResponse = new ApiResponse<>();
        apiResponse.setData(promotionPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findAllPromotions(
            @RequestParam(required = false) final String code,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PromotionDTO> promotionPageResponse = promotionService.findAllPaginationWithSearch(code, startDate, endDate);
        ApiResponse<List<PromotionDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(promotionPageResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    // Get a single promotion by id
//    @GetMapping("/{id}")
//    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
//        Optional<PromotionDTO> promotion = promotionService.getPromotionById(id);
//        return promotion.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    // Create a new promotion
    @PostMapping
    @Operation(summary = "Create a new promotion", description = "Adds a new promotion to the system")
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO createdPromotion = promotionService.addPromotion(promotionDTO);
        ApiResponse<PromotionDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(createdPromotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // Update an existing promotion
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDTO promotionDTO) {
//        PromotionDTO updatedPromotion = promotionService.updatePromotion(id, promotionDTO);
//        ApiResponse<PromotionDTO> apiResponse = new ApiResponse<>();
//        apiResponse.setData(updatedPromotion);
//        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
//    }

    @PutMapping("/{code}")
    @Operation(summary = "Update an existing promotion", description = "Updates a promotion identified by its code")
    public ResponseEntity<?> updatePromotion(@PathVariable String code , @Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO updatedPromotion = promotionService.updatePromotion(code, promotionDTO);
        ApiResponse<PromotionDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(updatedPromotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    // Delete a promotion
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a promotion", description = "Removes a promotion identified by its ID from the system")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);;
        return ResponseEntity.status(HttpStatus.CREATED).body("Xoa thanh cong");
    }
}
