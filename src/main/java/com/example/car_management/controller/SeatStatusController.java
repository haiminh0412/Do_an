package com.example.car_management.controller;

import com.example.car_management.dto.SeatStatus;
import com.example.car_management.service.SeatStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-status")
public class SeatStatusController {
    @Autowired
    private SeatStatusService seatStatusService;

    @PostMapping
    public SeatStatus save(@RequestBody SeatStatus seatStatus) {
        return seatStatusService.save(seatStatus);
    }

    @GetMapping
    public List<SeatStatus> findAll() {
        return seatStatusService.findAll();
    }
}
