package com.example.car_management.repository;

import com.example.car_management.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(final String code);

    @Query(value = "SELECT * FROM promotion " +
            "WHERE (:code IS NULL OR code LIKE %:code%) " +
            "AND (:startDate IS NULL OR start_date >= :startDate) " +
            "AND (:endDate IS NULL OR end_date <= :endDate)", nativeQuery = true)
    List<Promotion> findPromotionsByAdvancedSearch(@Param("code") String code,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}

