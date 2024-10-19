package com.example.car_management.repository;

import com.example.car_management.entity.Seat;
import com.example.car_management.entity.SeatHold;
import com.example.car_management.entity.TripDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISeatHoldRepository extends JpaRepository<SeatHold, Integer> {
    List<SeatHold> findByTripDetailAndHoldStartAfter(TripDetail tripDetail, LocalDateTime expiryTime);
    List<SeatHold> findBySessionId(String sessionId);


    @Query(value = "SELECT sh.seat_id " +
            "FROM seathold sh " +
            "INNER JOIN seat s ON sh.seat_id = s.seat_id " +
            "INNER JOIN trip_detail td ON td.trip_detail_id = sh.trip_detail_id " +
            "WHERE td.trip_detail_id = :tripDetailId " +
            "AND sh.departure_date = :departureDate",
            nativeQuery = true)
    HashSet<Integer> findSeatsByTripDetailIdAndDepartureDate(
            @Param("tripDetailId") Integer tripDetailId,
            @Param("departureDate") LocalDate departureDate
    );

    Optional<SeatHold> findBySeatAndTripDetailAndDepartureDate(Seat seat, TripDetail tripDetail, LocalDate departureDate);
    SeatHold findBySeat(Seat seat);
}