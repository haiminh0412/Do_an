package com.example.car_management.repository;

import com.example.car_management.entity.Booking;
import com.example.car_management.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Integer> {
    @Query(value = "SELECT " +
            "c.customer_id AS customerId, " +
            "c.name AS customerName, " +
            "c.phone, " +
            "c.email, " +
            "b.Booking_ID AS bookingId, " +
            "b.start_destination AS startDestination, " +
            "b.end_destination AS endDestination, " +
            "b.departure_date AS departureDate, " +
            "td.departure_time AS departureTime, " +
            "td.destination_time AS destinationTime, " +
            "t.departure AS tripDeparture, " +
            "t.destination AS tripDestination " +
            "FROM Booking b " +
            "JOIN Customer c ON b.customer_id = c.customer_id " +
            "JOIN Trip_Detail td ON b.Trip_Detail_ID = td.Trip_Detail_ID " +
            "JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "WHERE TIMESTAMPDIFF(MINUTE, NOW(), CONCAT(b.departure_date, ' ', td.departure_time)) BETWEEN 0 AND 60",
            nativeQuery = true)
    List<Object[]> findUpcomingBookings();

    boolean existsByBooking(Booking booking);
}
