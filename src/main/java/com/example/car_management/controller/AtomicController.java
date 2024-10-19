package com.example.car_management.controller;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Data
@Builder
public class AtomicController {
    protected final AtomicLong counter = new AtomicLong();
}
