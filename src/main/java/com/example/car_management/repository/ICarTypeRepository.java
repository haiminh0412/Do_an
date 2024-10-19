package com.example.car_management.repository;

import com.example.car_management.entity.CarType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICarTypeRepository extends JpaRepository<CarType, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndCarTypeIdNot(final String name, final Integer carTypeId);

    @RestResource(path = "findByNameContainingIgnoreCasePaged")
    Page<CarType> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @RestResource(path = "findByNameContainingIgnoreCase")
    List<CarType> findByNameContainingIgnoreCase(String name);
}
