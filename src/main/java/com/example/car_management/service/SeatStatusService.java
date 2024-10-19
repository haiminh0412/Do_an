package com.example.car_management.service;

import com.example.car_management.dto.SeatStatus;
import com.example.car_management.entity.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class SeatStatusService {
    public static final String HASH_KEY = "SeatStatus";

    @Autowired
    private RedisTemplate template;

    public SeatStatus save(SeatStatus seatStatus) {
        // Chuyển seatId từ Integer thành String trước khi lưu vào Redis
        template.opsForHash().put(HASH_KEY, String.valueOf(seatStatus.getSeatId()), seatStatus);
        return seatStatus;
    }

    public List<SeatStatus> findAll() {
        return template.opsForHash().values(HASH_KEY);
    }



//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate; // Redis cho trạng thái ghế
//
//    private final String SEAT_STATUS_KEY_PREFIX = "seat_status_";
//    private final String USER_HOLD_COUNT_KEY_PREFIX = "user_hold_count_";
//
//    // Hàm giữ chỗ
//    public boolean holdSeat(Long seatId, Long tripDetailId, LocalDate departureDate, String sessionId) {
//        String seatKey = SEAT_STATUS_KEY_PREFIX + seatId + "_" + tripDetailId + "_" + departureDate.toString();
//
//        // Kiểm tra nếu ghế đã được giữ
//        if (redisTemplate.hasKey(seatKey)) {
//            return false; // Ghế đã được giữ
//        }
//
//        // Lấy số lượng ghế mà người dùng đang giữ
//        String userHoldCountKey = USER_HOLD_COUNT_KEY_PREFIX + sessionId;
//        Integer holdCount = (Integer) redisTemplate.opsForValue().get(userHoldCountKey);
//        if (holdCount == null) {
//            holdCount = 0;
//        }
//
//        // Kiểm tra nếu người dùng đã giữ đủ 4 ghế
//        if (holdCount >= 4) {
//            return false; // Đã đạt giới hạn giữ chỗ
//        }
//
//        // Lưu ghế đang giữ vào Redis (với thời gian hết hạn 10 phút)
//        redisTemplate.opsForValue().set(seatKey, sessionId, Duration.ofMinutes(10));
//
//        // Tăng số lượng ghế mà người dùng đang giữ
//        redisTemplate.opsForValue().set(userHoldCountKey, holdCount + 1, Duration.ofMinutes(10));
//
//        return true; // Giữ ghế thành công
//    }
//
//    // Hàm hủy giữ chỗ
//    public boolean releaseSeat(Long seatId, Long tripDetailId, LocalDate departureDate, String sessionId) {
//        String seatKey = SEAT_STATUS_KEY_PREFIX + seatId + "_" + tripDetailId + "_" + departureDate.toString();
//
//        // Kiểm tra nếu ghế đang được giữ bởi người dùng này
//        String holderSessionId = (String) redisTemplate.opsForValue().get(seatKey);
//        if (sessionId.equals(holderSessionId)) {
//            redisTemplate.delete(seatKey); // Hủy ghế
//
//            // Giảm số lượng ghế mà người dùng đang giữ
//            String userHoldCountKey = USER_HOLD_COUNT_KEY_PREFIX + sessionId;
//            Integer holdCount = (Integer) redisTemplate.opsForValue().get(userHoldCountKey);
//            if (holdCount != null && holdCount > 0) {
//                redisTemplate.opsForValue().set(userHoldCountKey, holdCount - 1, Duration.ofMinutes(10));
//            }
//
//            return true; // Hủy thành công
//        }
//
//        return false; // Không có ghế nào được giữ bởi người dùng này
//    }
//
//    // Hàm kiểm tra số lượng ghế đang giữ
//    public int getUserHoldCount(String sessionId) {
//        String userHoldCountKey = USER_HOLD_COUNT_KEY_PREFIX + sessionId;
//        Integer holdCount = (Integer) redisTemplate.opsForValue().get(userHoldCountKey);
//        return holdCount != null ? holdCount : 0;
//    }
}
