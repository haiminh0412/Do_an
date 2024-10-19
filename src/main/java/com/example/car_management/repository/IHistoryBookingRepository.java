package com.example.car_management.repository;

import com.example.car_management.entity.HistoryBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistoryBookingRepository extends JpaRepository<HistoryBooking, Integer> {
    @Query(value = "SELECT * FROM History_Booking WHERE Booking_ID = :bookingId", nativeQuery = true)
    List<HistoryBooking> findByBookingId(@Param("bookingId") Integer bookingId);
}

