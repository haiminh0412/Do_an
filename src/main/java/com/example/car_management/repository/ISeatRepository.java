package com.example.car_management.repository;

import com.example.car_management.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface ISeatRepository extends JpaRepository<Seat, Integer> {
    @RestResource(path = "existsBySeatNumber")
    @Query(value =
            "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Seat s " +
            "WHERE s.car_id = :carId AND s.seat_number = :seatNumber AND s.seat_id <> :seatId", nativeQuery = true)
    int existsBySeatNumber(@Param("carId") Integer carId, @Param("seatNumber") String seatNumber, @Param("seatId") Integer seatId);

    @RestResource(path = "existsBySeatNumberBasic")
    @Query(value =
            "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Seat s " +
            "WHERE s.car_id = :carId AND s.seat_number = :seatNumber", nativeQuery = true)
    int existsBySeatNumber(@Param("carId") Integer carId, @Param("seatNumber") String seatNumber);

    @Query(value =
            "SELECT s.seat_id, s.car_id, s.seat_number, s.x, s.y, s.created_at, s.updated_at " +
            "FROM Seat s " +
            "INNER JOIN Car c " +
            "ON s.car_id = c.car_id " +
            "WHERE c.car_id = :carId", nativeQuery = true)
    List<Seat> findAllSeatIdByCar(@Param("carId") Integer carId);

    Seat findSeatByseatNumber(String seatNumber);
}
