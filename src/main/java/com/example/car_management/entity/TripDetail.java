package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trip_detail")
public class  TripDetail extends AbstractEnity{
    public static final String TABLE_NAME = "trip_detail";
    public static final String TRIP_DETAIL_ID = "tripDetailId";
    public static final String PRICE = "price";
    public static final String DEPARTURE_TIME = "departureTime";
    public static final String DESTINATION_TIME = "destinationTime";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String CAR = "car";
    public static final String TRIP = "trip";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Trip_Detail_ID")
    private Integer tripDetailId;

    @Column(name = "price")
    private Long price;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "destination_time")
    private LocalTime destinationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Trip_ID", nullable = false, referencedColumnName = "Trip_ID")
    @JsonBackReference
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Car_ID", nullable = false, referencedColumnName = "Car_ID")
    @JsonBackReference
    private Car car;

    @OneToMany(
            mappedBy = "tripDetail",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Booking> bookings = new HashSet<>();

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setTripDetail(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setTripDetail(null);
    }

    @OneToMany(
            mappedBy = "tripDetail",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<SeatHold> seatHolds = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addSeatHold(SeatHold seatHold) {
        seatHolds.add(seatHold);
        seatHold.setTripDetail(this);
    }

    public void removeSeatHold(SeatHold seatHold) {
        seatHolds.remove(seatHold);
        seatHold.setTripDetail(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip )) return false;
        return tripDetailId != null && tripDetailId.equals(((TripDetail) o).getTripDetailId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TripDetail{" +
                "tripDetailId=" + tripDetailId +
                ", price=" + price +
                ", departureTime=" + departureTime +
                ", destinationTime=" + destinationTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}