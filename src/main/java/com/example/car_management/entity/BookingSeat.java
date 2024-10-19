package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "booking_seat")
public class BookingSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Booking_Seat_ID")
    private Integer bookingSeatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Booking_ID", nullable = false, referencedColumnName = "Booking_ID")
    @JsonBackReference
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Seat_ID", nullable = false, referencedColumnName = "Seat_ID")
    @JsonBackReference
    private Seat seat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingSeat )) return false;
        return bookingSeatId != null && bookingSeatId.equals(((BookingSeat) o).getBookingSeatId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BookingSeat{" +
                "bookingSeatId=" + bookingSeatId +
                '}';
    }
}