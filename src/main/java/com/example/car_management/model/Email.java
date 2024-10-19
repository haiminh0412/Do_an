package com.example.car_management.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Email {
    private String from;
    private String to;
    private String subject;
    private String body;
}
