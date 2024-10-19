package com.example.car_management.service;

import com.example.car_management.dto.SeatHoldDTO;
import com.example.car_management.entity.Seat;
import com.example.car_management.entity.SeatHold;
import com.example.car_management.entity.TripDetail;
import com.example.car_management.repository.ISeatHoldRepository;
import com.example.car_management.repository.ISeatRepository;
import com.example.car_management.repository.ITripDetailRepository;
import com.example.car_management.service.implement.SeatService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatHoldService {

    @Autowired
    private ISeatHoldRepository seatHoldRepository;

    @Autowired
    private ISeatRepository seatRepository;

    @Autowired
    private ITripDetailRepository tripDetailRepository;
    @Autowired
    private SeatService seatService;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public SeatHoldDTO holdSeat(String sessionId, Integer seatId, Integer tripDetailId, LocalDate departureDate) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new EntityNotFoundException("Seat not found"));
        TripDetail tripDetail = tripDetailRepository.findById(tripDetailId).orElseThrow(() -> new EntityNotFoundException("TripDetail not found"));

        SeatHold seatHold = SeatHold.builder()
                .sessionId(sessionId)
                .seat(seat)
                .tripDetail(tripDetail)
                .holdStart(LocalDateTime.now())
                .departureDate(departureDate)
                .build();

        seatHold = seatHoldRepository.save(seatHold);

        return mapToDTO(seatHold);
    }

    @Transactional
    public void cancelSeat(SeatHoldDTO seatHold) {
        Seat seat = modelMapper.map(seatService.findById(seatHold.getSeatId()), Seat.class);
        SeatHold findSeatHold = seatHoldRepository.findBySeat(seat);
        System.out.println(findSeatHold.getSeatHoldId());
        seatHoldRepository.deleteById(findSeatHold.getSeatHoldId());
    }

    // Tự động hủy chỗ ngồi sau 20 phút nếu không thanh toán
    @Scheduled(fixedRate = 60000) // Kiểm tra mỗi phút
    public void autoCancelExpiredSeats() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatHold> expiredHolds = seatHoldRepository.findAll().stream()
                .filter(seatHold -> Duration.between(seatHold.getHoldStart(), now).toMinutes() > 20)
                .collect(Collectors.toList());

        // Xóa tất cả những ghế giữ quá 20 phút
        seatHoldRepository.deleteAll(expiredHolds);
    }


    public List<SeatHoldDTO> getSeatHoldsBySessionId(String sessionId) {
        return seatHoldRepository.findBySessionId(sessionId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SeatHoldDTO> getSeatHolds() {
        return seatHoldRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private SeatHoldDTO mapToDTO(SeatHold seatHold) {
        return SeatHoldDTO.builder()
                .seatHoldId(seatHold.getSeatHoldId())
                .sessionId(seatHold.getSessionId())
                .holdStart(seatHold.getHoldStart())
                .seatId(seatHold.getSeat().getSeatId())
                .tripDetailId(seatHold.getTripDetail().getTripDetailId())
                .build();
    }
}
