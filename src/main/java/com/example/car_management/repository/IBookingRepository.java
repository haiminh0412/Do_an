package com.example.car_management.repository;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.entity.Booking;
import com.example.car_management.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    @Query(value = "SELECT b.Booking_ID, b.start_destination, b.end_destination, b.departure_date, " +
            "b.booking_at, b.status, t.departure, t.destination, td.price, td.departure_time, " +
            "td.destination_time, c.name AS car_type, car.license_plate, " +
            "GROUP_CONCAT(seat.Seat_number ORDER BY seat.Seat_number ASC) AS booked_seats " +
            "FROM Booking b " +
            "LEFT JOIN Booking_Seat bs ON b.Booking_ID = bs.Booking_ID " +
            "LEFT JOIN Seat seat ON bs.Seat_ID = seat.Seat_ID " +
            "JOIN Trip_Detail td ON b.Trip_Detail_ID = td.Trip_Detail_ID " +
            "JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "JOIN Car car ON td.Car_ID = car.Car_ID " +
            "JOIN CarType c ON car.CarType_ID = c.CarType_ID " +
            "JOIN Customer cu ON b.Customer_ID = cu.Customer_ID " +
            "WHERE cu.name = :name AND cu.email = :email AND cu.phone = :phone " +
            "GROUP BY b.Booking_ID, b.start_destination, b.end_destination, b.departure_date, " +
            "b.booking_at, b.status, t.departure, t.destination, td.price, td.departure_time, " +
            "td.destination_time, c.name, car.license_plate " +
            "ORDER BY b.booking_at DESC " +
            "LIMIT 500", nativeQuery = true)
    List<Object[]> getAllBookedTickets(@Param("name") String name,
                                       @Param("email") String email,
                                       @Param("phone") String phone);

    @Query(value = "SELECT b.Booking_ID, b.start_destination, b.end_destination, b.departure_date, " +
            "b.booking_at, b.status, t.departure, t.destination, td.price, td.departure_time, " +
            "td.destination_time, c.name AS car_type, car.license_plate, " +
            "GROUP_CONCAT(seat.Seat_number ORDER BY seat.Seat_number ASC) AS booked_seats " +
            "FROM Booking b " +
            "LEFT JOIN Booking_Seat bs ON b.Booking_ID = bs.Booking_ID " +
            "LEFT JOIN Seat seat ON bs.Seat_ID = seat.Seat_ID " +
            "JOIN Trip_Detail td ON b.Trip_Detail_ID = td.Trip_Detail_ID " +
            "JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "JOIN Car car ON td.Car_ID = car.Car_ID " +
            "JOIN CarType c ON car.CarType_ID = c.CarType_ID " +
            "JOIN Customer cu ON b.Customer_ID = cu.Customer_ID " +
            "WHERE b.booking_id = :bookingId " +
            "GROUP BY b.Booking_ID, b.start_destination, b.end_destination, b.departure_date, " +
            "b.booking_at, b.status, t.departure, t.destination, td.price, td.departure_time, " +
            "td.destination_time, c.name, car.license_plate " +
            "ORDER BY b.booking_at DESC " +
            "LIMIT 500", nativeQuery = true)
    List<Object[]> getBookedTicketByBookingId(@Param("bookingId") final Integer bookingId);

    @Query(value = "SELECT b.Booking_ID, b.start_destination, b.end_destination, b.departure_date, " +
            "b.booking_at, b.status, t.departure, t.destination, td.price, td.departure_time, " +
            "td.destination_time, c.name AS car_type, car.license_plate, " +
            "GROUP_CONCAT(seat.Seat_number ORDER BY seat.Seat_number ASC) AS booked_seats " +
            "FROM Booking b " +
            "LEFT JOIN Booking_Seat bs ON b.Booking_ID = bs.Booking_ID " +
            "LEFT JOIN Seat seat ON bs.Seat_ID = seat.Seat_ID " +
            "JOIN Trip_Detail td ON b.Trip_Detail_ID = td.Trip_Detail_ID " +
            "JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "JOIN Car car ON td.Car_ID = car.Car_ID " +
            "JOIN CarType c ON car.CarType_ID = c.CarType_ID " +
            "JOIN Customer cu ON b.Customer_ID = cu.Customer_ID " +
            "WHERE b.Booking_ID = :bookingId AND cu.name = :name AND cu.email = :email AND cu.phone = :phone " +
            "GROUP BY b.Booking_ID, b.start_destination, b.end_destination, b.departure_date, " +
            "b.booking_at, b.status, t.departure, t.destination, td.price, td.departure_time, " +
            "td.destination_time, c.name, car.license_plate " +
            "ORDER BY b.booking_at DESC " +
            "LIMIT 500", nativeQuery = true)
    List<Object[]> searchBooking(@Param("bookingId") Integer bookingId,
                                 @Param("name") String name,
                                 @Param("email") String email,
                                 @Param("phone") String phone);


    List<Booking> findByCustomer(Customer customer);

    @Query("SELECT b FROM Booking b WHERE DATE(b.bookingAt) = CURRENT_DATE")
    Page<Booking> findBookingsToday(Pageable pageable);

    @Query(value = "SELECT c.name, " +
            "CONCAT(t.departure, ' - ', t.destination) AS trip, " +
            "hb.seat_count AS ticketCount, " +
            "b.booking_at, " +
            "b.departure_date, " +
            "hb.total_price AS totalAmount, " +
            "b.status " +  // Thêm cột status vào SELECT
            "FROM Booking b " +
            "JOIN Customer c ON b.Customer_ID = c.Customer_ID " +
            "JOIN Trip_Detail td ON b.Trip_Detail_ID = td.Trip_Detail_ID " +
            "JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "LEFT JOIN Booking_Seat bs ON bs.Booking_ID = b.Booking_ID " +
            "LEFT JOIN History_Booking hb ON hb.Booking_ID = b.Booking_ID " +
            "WHERE DATE(b.booking_at) = CURRENT_DATE " +  // Chỉ lấy booking có ngày đặt là hôm nay
            "GROUP BY b.Booking_ID, c.name, t.departure, t.destination, hb.seat_count, b.booking_at, b.departure_date, hb.total_price, b.status",  // Thêm b.status vào GROUP BY
            nativeQuery = true)
    Page<Object[]> findBookingsWithDetails(Pageable pageable);

    @Query(value = "SELECT c.name, " +
            "CONCAT(t.departure, ' - ', t.destination) AS trip, " +
            "hb.seat_count AS ticketCount, " +
            "b.booking_at, " +
            "b.departure_date, " +
            "hb.total_price AS totalAmount, " +
            "b.status " +
            "FROM Booking b " +
            "JOIN Customer c ON b.Customer_ID = c.Customer_ID " +
            "JOIN Trip_Detail td ON b.Trip_Detail_ID = td.Trip_Detail_ID " +
            "JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "LEFT JOIN Booking_Seat bs ON bs.Booking_ID = b.Booking_ID " +
            "LEFT JOIN History_Booking hb ON hb.Booking_ID = b.Booking_ID " +
            "WHERE (:customerName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :customerName, '%'))) " +  // Lọc theo tên khách hàng nếu có
            "AND (:trip IS NULL OR CONCAT(t.departure, ' - ', t.destination) = :trip) " +  // Lọc theo chuyến đi nếu có
            "AND (:status IS NULL OR b.status = :status) " +  // Lọc theo trạng thái nếu có
            "AND DATE(b.booking_at) BETWEEN :startDate AND :endDate " +  // Lọc theo khoảng thời gian booking_at
            "GROUP BY b.Booking_ID, c.name, t.departure, t.destination, hb.seat_count, b.booking_at, b.departure_date, hb.total_price, b.status",
            nativeQuery = true)
    Page<Object[]> findBookingsWithDetails(
            @Param("customerName") String customerName,
            @Param("trip") String trip,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    List<Booking> findAllByStatusNot(String status);

    @Transactional
    @Query(value = "update booking set status = 'Cancelled' where booking_id =:bookingId", nativeQuery = true)
    void cancelBooking(@Param("bookingId") Integer bookingId);
}
