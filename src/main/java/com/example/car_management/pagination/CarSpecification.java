package com.example.car_management.pagination;


import com.example.car_management.entity.Car;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CarSpecification {

    public static Specification<Car> searchByCriteria(String licensePlate, String carTypeName, Integer numberOfSeats, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Điều kiện 1=1 để bắt đầu
            Predicate whereClause = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
            predicates.add(whereClause);

            if (licensePlate != null && !licensePlate.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("licensePlate")), "%" + licensePlate.toLowerCase() + "%"));
            }

            if (carTypeName != null && !carTypeName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("carType").get("name")), "%" + carTypeName.toLowerCase() + "%"));
            }

            // Tìm kiếm theo số ghế
            if (numberOfSeats != null) {
                // Nếu có giá trị khác -1, tìm kiếm chính xác số ghế
                predicates.add(criteriaBuilder.equal(root.get("numberOfSeats"), numberOfSeats));
            }
            else {
                System.out.println("a");
            }


            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%" + status.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}