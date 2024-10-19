package com.example.car_management.controller;

import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.SeatDTO;
import com.example.car_management.service.implement.SeatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/seat")
@Tag(name = "Seat Controller", description = "Quản lý thông tin ghế")
public class SeatController extends AtomicController {
    @Autowired
    SeatService seatService;

    @Autowired
    AtomicLong atomicLong;

    @GetMapping
    @Operation(summary = "Lấy tất cả ghế", description = "Trả về danh sách tất cả các ghế")
    public ResponseEntity<?> findAllSeats() {
        List<SeatDTO> seats = seatService.findAll();
        ApiResponse<List<SeatDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(seats);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin ghế theo ID", description = "Trả về thông tin ghế theo ID")
    public ResponseEntity<?> findSeatById(
            @PathVariable("id")
            @NotNull
            @Positive(message = "NOT_POSITIVE") int id) {
        SeatDTO seatResponse = seatService.findById(id);
        ApiResponse<SeatDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/status")
    @Operation(summary = "Trạng thái ghế", description = "Trả về trạng thái đơn giản")
    public ResponseEntity<?> status() {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData("Hello");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping()
    @Operation(summary = "Thêm ghế mới", description = "Tạo mới một ghế")
    public ResponseEntity<?> addSeat(@RequestBody @Valid SeatDTO seatRequest) {
        SeatDTO seatResponse = seatService.insert(seatRequest);
        ApiResponse<SeatDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Cập nhật ghế", description = "Cập nhật thông tin ghế theo ID")
    public ResponseEntity<?> updateSeat(@PathVariable("id") int id, @RequestBody SeatDTO seatRequest) {
        SeatDTO seatResponse = seatService.update(seatRequest, id);
        ApiResponse<SeatDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Xóa ghế", description = "Xóa ghế theo ID")
    public ResponseEntity<?> deleteSeat(@PathVariable("id") int id) {
        SeatDTO seatResponse = seatService.findById(id);
        seatService.deleteById(id);
        ApiResponse<SeatDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping(value = "/{carId}")
    @Operation(summary = "Thêm bản đồ ghế mới", description = "Tạo bản đồ ghế cho một chiếc xe theo ID")
    public ResponseEntity<?> addSeat(@PathVariable("carId") int carId, @RequestBody String[][] seatsMapRequest) {
        String[][] seatsMapResponse = seatService.createSeatsMap(carId, seatsMapRequest);
        ApiResponse<String[][]> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatsMapResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/seats-map/{carId}")
    @Operation(summary = "Lấy bản sơ đồ theo ID xe", description = "Trả về sơ đồ ghế cho một chiếc xe theo ID")
    public ResponseEntity<?> getAllSeatsMapByCar(@PathVariable("carId") Integer carId) {
        String[][] seatsMap = seatService.getAllSeatsMapByCarId(carId);
        ApiResponse<String[][]> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatsMap);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/seats-map")
    @Operation(summary = "Lấy tất cả sơ dồ ghế", description = "Trả về danh sách tất cả sơ đồ ghế")
    public ResponseEntity<?> getAllSeatsMap() {
        List<String[][]> seatsMap = seatService.getAllSeatsMap();
        ApiResponse<List<String[][]>> apiResponse = new ApiResponse<>();
        apiResponse.setData(seatsMap);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    // Giữ ghế và gửi thông báo tới tất cả người dùng
    @MessageMapping("/hold/{sessionId}/{seatId}/{tripDetailId}/{departureDate}")
    @SendTo("/topic/hold")
    @Operation(summary = "Giữ ghế", description = "Giữ ghế")
    public String holdSeat(
            @DestinationVariable String sessionId,
            @DestinationVariable Integer seatId,
            @DestinationVariable Integer tripDetailId,
            @DestinationVariable String departureDate) {

        boolean isHeld = seatService.holdSeat(sessionId, seatId, tripDetailId, departureDate); // Giữ ghế 20 phút
        return seatId + "," + (isHeld ? 0 : -1);
    }

    // Hủy giữ ghế và gửi thông báo tới tất cả người dùng
    @MessageMapping("/cancel/{sessionId}/{seatId}/{tripDetailId}/{departureDate}")
    @SendTo("/topic/cancel")
    @Operation(summary = "Hủy giữ ghế", description = "Hủy giữ ghế")
    public String cancelSeat(
            @DestinationVariable String sessionId,
            @DestinationVariable Integer seatId,
            @DestinationVariable Integer tripDetailId,
            @DestinationVariable String departureDate) {

        boolean isCancelled = seatService.cancelSeat(sessionId, seatId, tripDetailId, departureDate);
        return seatId + "," + (isCancelled ? 2 : -1);
    }

//    @MessageMapping("/hello/{seatId}/{tripDetailId}/{departureDate}")
//    @SendTo("/topic/hold")
//    public String holdSeat(
//            @DestinationVariable Integer seatId,
//            @DestinationVariable Integer tripDetailId,
//            @DestinationVariable LocalDate departureDate) {
//        SeatAvailability seatAvailability = seatService.holdSeat(seatId, tripDetailId, departureDate, 15); // Giữ ghế trong 15 phút
//
//        return seatAvailability.getSeat().getSeatId() + "," + seatAvailability.getStatus();
//    }
//
//    @MessageMapping("/hi/{seatId}/{tripDetailId}/{departureDate}")
//    @SendTo("/topic/cancel")
//    public String cancelSeat(
//            @DestinationVariable Integer seatId,
//            @DestinationVariable Integer tripDetailId,
//            @DestinationVariable LocalDate departureDate) {
//        SeatAvailability seatAvailability = seatService.cancelSeat(seatId, tripDetailId, departureDate); // Giữ ghế trong 15 phút
//        return seatAvailability.getSeat().getSeatId() + "," + seatAvailability.getStatus();
//    }

//    @MessageMapping("/reserveSeat")
//    @SendTo("/topic/seatStatus/{seatId}/{tripDetailId}/{departureDate}")
//    public ResponseEntity<?> reserveSeat(
//            @DestinationVariable Integer seatId,
//            @DestinationVariable Integer tripDetailId,
//            @DestinationVariable LocalDate departureDate) {
//        // Xử lý giữ ghế và trả về phản hồi
//        SeatAvailability seatAvailability = seatService.holdSeat(seatId, tripDetailId, departureDate, 15); // Giữ ghế trong 15 phút
//
//        ApiResponse<SeatAvailability> apiResponse = new ApiResponse<>();
//        apiResponse.setData(seatAvailability);
//
//        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
//    }

    // Khi client gửi yêu cầu hủy chỗ qua WebSocket
//    @MessageMapping("/cancelSeat")
//    @SendTo("/topic/seatStatus")
//    public ResponseEntity<?> cancelSeat(SeatRequest request) throws Exception {
//        // Logic xử lý hủy chỗ
//        Thread.sleep(1000); // Giả lập xử lý chậm
//        return new SeatStatus(request.getSeatId(), "available", null);
//    }

//    // Giữ ghế
//    @PostMapping("/{seatId}/hold")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> holdSeat(
//            @PathVariable("seatId") String seatId,
//            @RequestParam("tripDetailId") String tripDetailId,
//            @RequestParam("departureDate") @NotNull LocalDate departureDate,
//            @RequestParam("holdDurationMinutes") int holdDurationMinutes) {
//        return seatService.holdSeat(seatId, tripDetailId, departureDate, holdDurationMinutes)
//                .thenApply(v -> ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Seat held successfully")))
//                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage())));
//    }
//
//    // Kiểm tra thời gian giữ ghế đã hết hạn chưa
//    @GetMapping("/{seatId}/isHoldExpired")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> isHoldExpired(
//            @PathVariable("seatId") String seatId,
//            @RequestParam("tripDetailId") String tripDetailId,
//            @RequestParam("departureDate") @NotNull LocalDate departureDate) {
//        return seatService.isHoldExpired(seatId, tripDetailId, departureDate)
//                .thenApply(isExpired -> ResponseEntity.ok(new ApiResponse<>("Hold expired: " + isExpired)))
//                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage())));
//    }
//
//    // Hủy giữ ghế
//    @PostMapping("/{seatId}/releaseHold")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> releaseHold(
//            @PathVariable("seatId") String seatId,
//            @RequestParam("tripDetailId") String tripDetailId,
//            @RequestParam("departureDate") @NotNull LocalDate departureDate) {
//        return seatService.releaseHold(seatId, tripDetailId, departureDate)
//                .thenApply(v -> ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Hold released successfully")))
//                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage())));
//    }
//
//    // Đặt ghế
//    @PostMapping("/{seatId}/book")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> bookSeat(
//            @PathVariable("seatId") String seatId,
//            @RequestParam("tripDetailId") String tripDetailId,
//            @RequestParam("departureDate") @NotNull LocalDate departureDate) {
//        return seatService.bookSeat(seatId, tripDetailId, departureDate)
//                .thenApply(v -> ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Seat booked successfully")))
//                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage())));
//    }
//
//    // Cập nhật trạng thái ghế
//    @PatchMapping("/{seatId}/status")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> updateSeatStatus(
//            @PathVariable("seatId") String seatId,
//            @RequestParam("tripDetailId") String tripDetailId,
//            @RequestParam("departureDate") @NotNull LocalDate departureDate,
//            @RequestParam("status") int status) {
//        return seatService.updateSeatStatus(seatId, tripDetailId, departureDate, status)
//                .thenApply(v -> ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Seat status updated successfully")))
//                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(  new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage())));
//    }
}
