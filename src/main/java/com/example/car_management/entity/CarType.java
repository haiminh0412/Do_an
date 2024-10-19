package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cartype")
public class CarType extends AbstractEnity{
    public static final String TABLE_NAME = "cartype";
    public static final String CAR_TYPE_ID = "carTypeId";
    public static final String NAME = "name";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarType_ID")
    private Integer carTypeId;

    @Column(name = "name")
    private String name;

    @OneToMany(
            mappedBy = "carType",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @JsonManagedReference
    private Set<Car> cars = new HashSet<>();

    //Constructors, getters and setters removed for brevity

    public void addCar(Car car) {
        if(car != null) {
            cars.add(car);
            car.setCarType(this);
        }
    }

    public void removeCar(Car car) {
        if(car != null) {
            cars.remove(car);
            car.setCarType(null);
        }
    }

    @Override
    public String toString() {
        return "CarType{" +
                "carTypeId=" + carTypeId +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}