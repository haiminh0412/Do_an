package com.example.car_management.repository;

import com.example.car_management.entity.Booking;
import com.example.car_management.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;

@Repository
public interface IBookingSeatRepository extends JpaRepository<BookingSeat, Integer> {
    @Query(value = "SELECT seat_id " +
            "FROM booking_seat as bs " +
            "INNER JOIN booking as b " +
            "ON bs.booking_id = b.booking_id " +
            "WHERE departure_date >= CURDATE() AND departure_date = :departureDate AND trip_detail_id = :tripDetailID", nativeQuery = true)
    HashSet<Integer> findSeatsIdByDepartureDateAndTripDetail(@Param("departureDate") LocalDate departureDate, @Param("tripDetailID") Integer tripDetailID);

    @Transactional
    @Query(value = "DELETE FROM Booking_seat WHERE booking_id =:bookingId", nativeQuery = true)
    void cancelBookingSeat(@Param("bookingId") Integer bookingId);

    boolean existsByBooking(Booking booking);
}
