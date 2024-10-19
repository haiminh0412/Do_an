package com.example.car_management.controller;

import com.example.car_management.dto.SeatHoldDTO;
import com.example.car_management.service.SeatHoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seathold")
public class SeatHoldController {

    @Autowired
    private SeatHoldService seatHoldService;

//    @PostMapping("/hold")
//    public ResponseEntity<SeatHoldDTO> holdSeat(
//            @RequestParam String sessionId,
//            @RequestParam Integer seatId,
//            @RequestParam Integer tripDetailId) {
//        SeatHoldDTO seatHoldDTO = seatHoldService.holdSeat(sessionId, seatId, tripDetailId);
//        return ResponseEntity.ok(seatHoldDTO);
//    }

//    @DeleteMapping("/cancel/{seatHoldId}")
//    public ResponseEntity<Void> cancelSeat(@PathVariable Integer seatHoldId) {
//        seatHoldService.cancelSeat(seatHoldId);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SeatHoldDTO>> getSeatHoldsBySessionId(@PathVariable String sessionId) {
        List<SeatHoldDTO> seatHolds = seatHoldService.getSeatHoldsBySessionId(sessionId);
        return ResponseEntity.ok(seatHolds);
    }

    @GetMapping()
    public ResponseEntity<List<SeatHoldDTO>> getSeatHolds() {
        List<SeatHoldDTO> seatHolds = seatHoldService.getSeatHolds();
        return ResponseEntity.ok(seatHolds);
    }

    @Scheduled(fixedRate = 60000) // Run every minute to auto-cancel expired holds
    public void autoCancelExpiredSeats() {
        seatHoldService.autoCancelExpiredSeats();
    }

    @MessageMapping("/hold")
    @SendTo("/topic/hold")
    public SeatHoldDTO holdSeat(SeatHoldDTO seatHoldDTO) {
        // Xử lý giữ ghế và gửi thông báo đến các client
        seatHoldService.holdSeat(seatHoldDTO.getSessionId(), seatHoldDTO.getSeatId(), seatHoldDTO.getTripDetailId(), seatHoldDTO.getDepartureDate());
        return seatHoldDTO;
    }

    @MessageMapping("/cancel")
    @SendTo("/topic/cancel")
    public SeatHoldDTO cancelSeat(SeatHoldDTO seatHoldDTO) {
        // Xử lý hủy ghế và gửi thông báo đến các client
        seatHoldService.cancelSeat(seatHoldDTO);
        return seatHoldDTO;
    }
}
