package com.example.car_management.repository;

import com.example.car_management.entity.Car;
import com.example.car_management.entity.Trip;
import com.example.car_management.entity.TripDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ITripDetailRepository extends JpaRepository<TripDetail, Integer> {
    @RestResource(path = "existsByCarAndTripIds")
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Trip_Detail td " +
            "INNER JOIN Car c ON td.car_id = c.car_id " +
            "INNER JOIN Trip t ON td.trip_id = t.trip_id " +
            "WHERE c.car_id = :carId AND t.trip_id = :tripId AND td.trip_detail_id <> :tripDetailId", nativeQuery = true)
    int existsByCarAndTrip(@Param("carId") Integer carId, @Param("tripId") Integer tripId, @Param("tripDetailId") Integer tripDetailId);


    @RestResource(path = "existsByCarAndTripEntities")
    boolean existsByCarAndTrip(Car car, Trip trip);
    boolean existsByCarAndTripAndTripDetailIdNot(final Car car, final Trip trip, final Integer tripDetailId);

    @Query(value = "SELECT COUNT(*) AS COUNT_SEAT_BOOKED " +
            "FROM TRIP_DETAIL AS td " +
            "INNER JOIN BOOKING AS b " +
            "ON td.trip_detail_id = b.trip_detail_id " +
            "INNER JOIN BOOKING_SEAT AS bs " +
            "ON bs.booking_id = b.booking_id " +
            "WHERE td.trip_detail_id = :tripDetailId AND departure_date = :departureDate", nativeQuery = true)
    Integer countBookedSeats(@Param("tripDetailId") Integer tripDetailId, LocalDate departureDate);

    @Query(value = "SELECT td.*, t.departure, t.destination, c.license_plate, ct.name AS car_type_name " +
            "FROM Trip_Detail td " +
            "INNER JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "INNER JOIN Car c ON td.Car_ID = c.Car_ID " +
            "INNER JOIN CarType ct ON c.CarType_ID = ct.CarType_ID " +
            "WHERE (:departure IS NULL OR t.departure LIKE CONCAT('%', :departure, '%')) AND " +
            "(:destination IS NULL OR t.destination LIKE CONCAT('%', :destination, '%')) AND " +
            "(:licensePlate IS NULL OR c.license_plate LIKE CONCAT('%', :licensePlate, '%')) AND " +
            "(:carTypeName IS NULL OR ct.name LIKE CONCAT('%', :carTypeName, '%')) AND " +
            "(:minPrice IS NULL OR :maxPrice IS NULL OR td.price BETWEEN :minPrice AND :maxPrice) AND " +
            "(:startTime IS NULL OR :endTime IS NULL OR " +
            "(td.departure_time BETWEEN :startTime AND :endTime AND td.destination_time BETWEEN :startTime AND :endTime))",
            nativeQuery = true)
    List<TripDetail> searchTrips(
            @Param("departure") String departure,
            @Param("destination") String destination,
            @Param("licensePlate") String licensePlate,
            @Param("carTypeName") String carTypeName,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query(value = "SELECT td.*, t.departure, t.destination, c.license_plate, ct.name AS car_type_name " +
            "FROM Trip_Detail td " +
            "INNER JOIN Trip t ON td.Trip_ID = t.Trip_ID " +
            "INNER JOIN Car c ON td.Car_ID = c.Car_ID " +
            "INNER JOIN CarType ct ON c.CarType_ID = ct.CarType_ID " +
            "WHERE (:departure IS NULL OR t.departure LIKE CONCAT('%', :departure, '%')) AND " +
            "(:destination IS NULL OR t.destination LIKE CONCAT('%', :destination, '%')) AND " +
            "(:licensePlate IS NULL OR c.license_plate LIKE CONCAT('%', :licensePlate, '%')) AND " +
            "(:carTypeName IS NULL OR ct.name LIKE CONCAT('%', :carTypeName, '%')) AND " +
            "(:minPrice IS NULL OR :maxPrice IS NULL OR td.price BETWEEN :minPrice AND :maxPrice) AND " +
            "(:startTime IS NULL OR :endTime IS NULL OR " +
            "(td.departure_time BETWEEN :startTime AND :endTime AND td.destination_time BETWEEN :startTime AND :endTime))",
            nativeQuery = true)
    Page<TripDetail> searchTrips(
            @Param("departure") String departure,
            @Param("destination") String destination,
            @Param("licensePlate") String licensePlate,
            @Param("carTypeName") String carTypeName,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            Pageable pageable
    );

    @Query("SELECT td FROM TripDetail td WHERE td.car.carId = :carId " +
            "AND (:departureTime < td.destinationTime AND :destinationTime > td.departureTime)")
    List<TripDetail> findOverlappingTrips(
            @Param("carId") Integer carId,
            @Param("departureTime") LocalTime departureTime,
            @Param("destinationTime") LocalTime destinationTime
    );

    @Override
    Page<TripDetail> findAll(Pageable pageable);
}
