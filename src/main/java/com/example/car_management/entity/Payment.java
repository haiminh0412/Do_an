package com.example.car_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Payment_ID")
    private Integer paymentId;

    @Column(name = "Booking_ID")
    private Integer bookingId;

    @Column(name = "Payment_Method")
    private String paymentMethod;

    @Column(name = "payement_at", insertable = false, updatable = false)
    private LocalDateTime payementAt;
}
