package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "history_booking")
public class HistoryBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "History_ID")
    private Integer historyId;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "seat_count") // Thêm cột seat_count
    private Integer seatCount; // Thuộc tính số ghế


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "Booking_ID", nullable = false, referencedColumnName = "Booking_ID")
    @JsonBackReference
    private Booking booking; // Thêm quan hệ với Booking

    @Override
    public String toString() {
        return "HistoryBooking{" +
                "totalPrice=" + totalPrice +
                ", seatCount=" + seatCount + // Cập nhật toString để bao gồm số ghế
                ", historyId=" + historyId +
                '}';
    }
}
