package com.example.car_management.repository;

import com.example.car_management.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITripRepository extends JpaRepository<Trip, Integer> {
    boolean existsByDepartureAndDestination(final String departure, final String destination);

    boolean existsByDepartureAndDestinationAndTripIdNot(final String departure, final String destination, final Integer tripId);

    @Query(value = "SELECT DISTINCT departure " +
            "FROM Trip " +
            "ORDER BY departure" , nativeQuery = true)
    List<String> getAllDeparture();

    @Query(value = "SELECT DISTINCT destination " +
            "FROM Trip " +
            "ORDER BY destination" , nativeQuery = true)
    List<String> getAllDestination();

    @Override
    Page<Trip> findAll(Pageable pageable);

    @Query(value = "SELECT CONCAT(departure, ' - ', destination) AS trip " +
            "FROM Trip", nativeQuery = true)
    List<String> findAllTripsWithFormat();

    @Query(value = "SELECT * FROM Trip " +
            "WHERE (:departure IS NULL OR departure = :departure) " +
            "AND (:destination IS NULL OR destination = :destination)", nativeQuery = true)
    List<Trip> searchTrips(@Param("departure") String departure, @Param("destination") String destination);


    @Query(value = "SELECT * FROM Trip " +
            "WHERE (:departure IS NULL OR departure = :departure) " +
            "AND (:destination IS NULL OR destination = :destination)", nativeQuery = true)
    Page<Trip> searchTrips(@Param("departure") String departure, @Param("destination") String destination, Pageable pageable);

}