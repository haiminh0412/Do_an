package com.example.car_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatAvailability {
    public static final int EMPTY = 0;
    public static final int BOOKED = 1;
    public static final int HOLD = 2;

    private SeatDTO seat;

    private TripDetailDTO tripDetail;

    private LocalDate departureDate;

    private int status = EMPTY;

    private LocalDateTime holdExpiryTime;

    // Phương thức để đặt giữ ghế
//    public void holdSeat(int holdDurationMinutes) {
//        this.status = HOLD;
//        this.holdExpiryTime = LocalDateTime.now().plusMinutes(holdDurationMinutes);
//    }
//
//    // Phương thức để kiểm tra xem thời gian giữ ghế đã hết hạn chưa
//    public boolean isHoldExpired() {
//        return this.status == HOLD && LocalDateTime.now().isAfter(holdExpiryTime);
//    }
//
//    // Phương thức để hủy giữ ghế
//    public void releaseHold() {
//        if (isHoldExpired()) {
//            this.status = EMPTY;
//            this.holdExpiryTime = null;
//        }
//    }
//
//    // Phương thức để hoàn tất đặt ghế
//    public void bookSeat() {
//        if (this.status == HOLD) {
//            this.status = BOOKED;
//            this.holdExpiryTime = null;
//        }
//    }
}
