package com.example.car_management.entity;

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
@Table(name = "trip")
public class Trip extends AbstractEnity{
    public static final String TABLE_NAME = "trip";
    public static final String TRIP_ID = "tripId";
    public static final String DEPARTURE = "departure";
    public static final String DESTINATION = "destination";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Trip_ID")
    private Integer tripId;

    @Column(name = "departure")
    private String departure;

    @Column(name = "destination")
    private String destination;

    @OneToMany(
            mappedBy = "trip",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<TripDetail> tripDetails = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addTripDetail(TripDetail tripDetail) {
        if(tripDetail != null) {
            tripDetails.add(tripDetail);
            tripDetail.setTrip(this);
        }
    }

    public void removeTripDetail(TripDetail tripDetail) {
        if(tripDetail != null) {
            tripDetails.remove(tripDetail);
            tripDetail.setTrip(null);
        }
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
