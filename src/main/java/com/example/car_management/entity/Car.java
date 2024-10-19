package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "car")
public class Car extends AbstractEnity{
    public static final String TABLE_NAME = "car";
    public static final String SEATS = "seats";
    public static final String LICENSE_PLATE = "license_plate";
    public static final String IMAGE = "image";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String CAR_ID = "carId";
    public static final String CAR_TYPE = "carType";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Car_ID")
    private Integer carId;

    @Column(name = "image")
    private String image;

    @Column(name = "seats")
    private Integer numberOfSeats;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarType_ID", nullable = false, referencedColumnName = "CarType_ID")
    @JsonBackReference
    private CarType carType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car )) return false;
        return carId != null && carId.equals(((Car) o).getCarId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @OneToMany(
            mappedBy = "car",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<TripDetail> tripDetails = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addTripDetail(TripDetail tripDetail) {
        tripDetails.add(tripDetail);
        tripDetail.setCar(this);
    }

    public void removeTripDetail(TripDetail tripDetail) {
        tripDetails.remove(tripDetail);
        tripDetail.setCar(null);
    }

    @OneToMany(
            mappedBy = "car",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Seat> seatList = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addSeat(Seat seat) {
        seatList.add(seat);
        seat.setCar(this);
    }

    public void removeSeat(Seat seat) {
        seatList.remove(seat);
        seat.setCar(null);
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", seats=" + numberOfSeats +
                ", licensePlate='" + licensePlate + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}