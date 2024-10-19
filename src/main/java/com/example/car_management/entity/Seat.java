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
@Table(name = "seat")
public class Seat extends AbstractEnity{
    public static final String TABLE_NAME = "seat";
    public static final String SEAT_ID = "seatId";
    public static final String SEAT_NUMBER = "seatNumber";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String CAR = "car";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Seat_ID")
    private Integer seatId;

    @Column(name = "Seat_number")
    private String seatNumber;

    @Column(name = "x")
    private Integer x;

    @Column(name = "y")
    private Integer y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Car_ID", nullable = false, referencedColumnName = "Car_ID")
    @JsonBackReference
    private Car car;

    @OneToMany(
            mappedBy = "seat",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<BookingSeat> bookingSeats = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addBookingSeat(BookingSeat bookingSeat) {
        bookingSeats.add(bookingSeat);
        bookingSeat.setSeat(this);
    }

    public void removeBookingSeat(BookingSeat bookingSeat) {
        bookingSeats.remove(bookingSeat);
        bookingSeat.setSeat(null);
    }

    @OneToMany(
            mappedBy = "seat",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<SeatHold> seatHolds = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addSeatHold(SeatHold seatHold) {
        seatHolds.add(seatHold);
        seatHold.setSeat(this);
    }

    public void removeSeatHold(SeatHold seatHold) {
        seatHolds.remove(seatHold);
        seatHold.setSeat(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat )) return false;
        return seatId != null && seatId.equals(((Seat) o).getSeatId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", seatNumber='" + seatNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}