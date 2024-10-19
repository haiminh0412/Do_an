package com.example.car_management.repository;

import com.example.car_management.entity.Booking;
import com.example.car_management.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IVerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    VerificationCode findByCodeAndBooking(final String code, final Booking booking);
    Optional<VerificationCode> findByBookingAndType(Booking booking, String type);
}
