package com.example.car_management.repository;

import com.example.car_management.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    boolean existsBylicensePlate(final String licensePlate);
    boolean existsBylicensePlateAndCarIdNot(final String licensePlate, final Integer carId);

    @Override
    Page<Car> findAll(Pageable pageable);

    // Tìm kiếm với biển số xe (licensePlate), loại xe (carType), số ghế (numberOfSeats), và trạng thái (status)
    // Tìm kiếm xe dựa vào biển số, loại xe, số ghế và trạng thái
//    Page<Car> findByLicensePlateContainingIgnoreCaseAndCarType_NameContainingIgnoreCaseAndNumberOfSeatsAndStatusContainingIgnoreCase(
//            String licensePlate,
//            String carTypeName,
//            Integer numberOfSeats,
//            String status,
//            Pageable pageable);
//
//
//    List<Car> findByLicensePlateContainingIgnoreCaseAndCarType_NameContainingIgnoreCaseAndNumberOfSeatsAndStatusContainingIgnoreCase(
//            String licensePlate, String carTypeName, Integer numberOfSeats, String status);

    @Query(value = "SELECT c.Car_ID, c.license_plate, c.seats, c.status, c.image, c.CarType_ID , ct.name, ct.created_at, ct.updated_at " +
            "FROM Car c " +
            "INNER JOIN CarType ct ON c.CarType_ID = ct.CarType_ID " +
            "WHERE (:licensePlate IS NULL OR c.license_plate LIKE CONCAT('%', :licensePlate, '%')) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:numberOfSeats IS NULL OR c.seats = :numberOfSeats) AND " +
            "(:carTypeName IS NULL OR ct.name LIKE CONCAT('%', :carTypeName, '%'))",
            nativeQuery = true)
    List<Car> searchCars(
            @Param("licensePlate") String licensePlate,
            @Param("numberOfSeats") Integer numberOfSeats,
            @Param("carTypeName") String carTypeName,
            @Param("status") String status);

    @Query(value = "SELECT c.Car_ID, c.license_plate, c.seats, c.status, c.image, c.CarType_ID, ct.name, ct.created_at, ct.updated_at " +
            "FROM Car c " +
            "INNER JOIN CarType ct ON c.CarType_ID = ct.CarType_ID " +
            "WHERE (:licensePlate IS NULL OR c.license_plate LIKE CONCAT('%', :licensePlate, '%')) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:numberOfSeats IS NULL OR c.seats = :numberOfSeats) AND " +
            "(:carTypeName IS NULL OR ct.name LIKE CONCAT('%', :carTypeName, '%'))",
            nativeQuery = true)
    Page<Car> searchCars(
            @Param("licensePlate") String licensePlate,
            @Param("numberOfSeats") Integer numberOfSeats,
            @Param("carTypeName") String carTypeName,
            @Param("status") String status,
            Pageable pageable);

    // Hoặc nếu muốn cho phép một số trường có thể không có giá trị:
//    Page<Car> findByLicensePlateContainingIgnoreCaseAndCarType_NameContainingIgnoreCaseAndNumberOfSeatsAndStatus(
//            String licensePlate, String carType, Integer numberOfSeats, String status, Pageable pageable);
//    Page<Car> findByNameContainingIgnoreCase(String name, Pageable pageable);
//
//    List<Car> findByNameContainingIgnoreCase(String name);
}
