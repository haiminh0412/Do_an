package com.example.car_management.service;

import com.example.car_management.dto.CarDTO;
import com.example.car_management.dto.SeatAvailability;
import com.example.car_management.dto.SeatDTO;
import com.example.car_management.dto.TripDetailDTO;
import com.example.car_management.entity.Seat;
import com.example.car_management.repository.ISeatHoldRepository;
import com.example.car_management.repository.ISeatRepository;
import com.example.car_management.service.implement.BookingSeatService;
import com.example.car_management.service.implement.CarService;
import com.example.car_management.service.implement.SeatService;
import com.example.car_management.service.implement.TripDetailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class SeatAvailabilitieService {
    @Autowired
    private TripDetailService tripDetailService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private CarService carService;

    @Autowired
    private BookingSeatService bookingSeatService;

    @Autowired
    private ISeatHoldRepository seatHoldRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//
//    private Map<SeatAvailability, LocalDateTime> seatHoldMap = new HashMap<>();
//
//    // Logic giữ ghế
//    public void holdSeat(SeatDTO seatDTO, Integer tripDetailId, LocalDate departureDate) {
//        // Tạo đối tượng SeatAvailability từ DTO
//        SeatAvailability seatAvailability = SeatAvailability.builder()
//                .seat(seatDTO)
//                .tripDetail(tripDetailService.findById(tripDetailId))
//                .departureDate(departureDate)
//                .status(SeatAvailability.HOLD)
//                .build();
//
//        // Lưu thời gian giữ ghế vào bộ nhớ
//        seatHoldMap.put(seatAvailability, LocalDateTime.now());
//
//        // Gửi thông báo trạng thái ghế qua WebSocket
//        messagingTemplate.convertAndSend("/topic/seat-status", seatAvailability);
//    }
//
//    // Logic hủy ghế
//    public void releaseSeat(SeatDTO seatDTO, Integer tripDetailId, LocalDate departureDate) {
//        // Xóa thông tin giữ ghế khỏi bộ nhớ
//        seatHoldMap.remove(seatDTO.getSeatId());
//
//        // Tạo đối tượng SeatAvailability từ DTO
//        SeatAvailability seatAvailability = SeatAvailability.builder()
//                .seat(seatDTO)
//                .tripDetail(tripDetailService.findById(tripDetailId))
//                .departureDate(departureDate)
//                .status(SeatAvailability.EMPTY)
//                .build();
//
//        // Xóa thông tin giữ ghế khỏi bộ nhớ
//        seatHoldMap.entrySet().removeIf(entry -> entry.getKey().equals(seatAvailability));
//
//        // Gửi thông báo trạng thái ghế qua WebSocket
//        messagingTemplate.convertAndSend("/topic/seat-status", seatAvailability);
//    }
//
//    @Scheduled(fixedRate = 60000) // Mỗi phút
//    public void checkSeatHoldStatus() {
//        LocalDateTime now = LocalDateTime.now();
//        Iterator<Map.Entry<SeatAvailability, LocalDateTime>> iterator = seatHoldMap.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            Map.Entry<SeatAvailability, LocalDateTime> entry = iterator.next();
//            SeatAvailability seatAvailability = entry.getKey();
//            LocalDateTime holdTime = entry.getValue();
//
//            if (holdTime.plusMinutes(20).isBefore(now)) {
//                // Tạo đối tượng SeatAvailability với trạng thái đã hết thời gian giữ
//                SeatAvailability updatedSeatAvailability = SeatAvailability.builder()
//                        .seat(seatAvailability.getSeat())
//                        .tripDetail(seatAvailability.getTripDetail())
//                        .departureDate(seatAvailability.getDepartureDate())
//                        .status(SeatAvailability.EMPTY)
//                        .build();
//
//                // Xóa ghế đã hết thời gian giữ
//                iterator.remove();
//
//                // Gửi thông báo trạng thái ghế qua WebSocket
//                messagingTemplate.convertAndSend("/topic/seat-status", updatedSeatAvailability);
//            }
//        }
//    }

    public SeatAvailability[][] getSeatMaps(final Integer tripDetailId, final LocalDate departureDate) {
        TripDetailDTO tripDetail = tripDetailService.findById(tripDetailId);
        CarDTO car = carService.findById(tripDetail.getCar().getCarId());
        Integer numberSeats = car.getNumberOfSeats();

        String[][] seatsMap = seatService.getAllSeatsMapByCarId(car.getCarId());

        if(seatsMap.length == 0)
            return new SeatAvailability[][] {};

        int row = seatsMap.length;
        int col = seatsMap[0].length;

        SeatAvailability[][] seatAvailabilitiesMap = new SeatAvailability[row][col];
        List<SeatAvailability> seatAvailabilities = this.getAvailableSeats(tripDetailId, departureDate);

        int index = 0;
        for(int i = 0; i < row; ++i) {
            for(int j = 0; j < col; ++j) {
                if(seatsMap[i][j] != null && !seatsMap[i][j].isEmpty()) {
                    seatAvailabilitiesMap[i][j] = seatAvailabilities.get(index++);
                }
            }
        }
        return seatAvailabilitiesMap;
    }
    
    public List<SeatAvailability> getAvailableSeats(Integer tripDetailId, LocalDate departureDate) {
        TripDetailDTO tripDetail = tripDetailService.findById(tripDetailId);
        Integer carId = tripDetail.getCar().getCarId();
        List<SeatDTO> seats = seatService.findAllSeatByCar(carId);

        List<SeatAvailability> seatAvailabilities = new ArrayList<>();
        for (SeatDTO seat : seats) {
            SeatAvailability seatAvailability = SeatAvailability.builder()
                    .seat(seat)
                    .tripDetail(tripDetail)
                    .departureDate(departureDate)
                    .status(SeatAvailability.EMPTY)
                    .build();
            seatAvailabilities.add(seatAvailability);
        }

        Set<Integer> seatIds = bookingSeatService.findAllSeatIdByConditional(tripDetailId, departureDate);
        Set<Integer> seatHoldIds = seatHoldRepository.findSeatsByTripDetailIdAndDepartureDate(tripDetailId, departureDate);

        for(SeatAvailability seatAvailability : seatAvailabilities) {
            if(seatIds.contains(seatAvailability.getSeat().getSeatId())) {
                seatAvailability.setStatus(SeatAvailability.BOOKED);
            }
            else if(seatHoldIds.contains(seatAvailability.getSeat().getSeatId())) {
                seatAvailability.setStatus(SeatAvailability.HOLD);
            }
            else {
                seatAvailability.setStatus(SeatAvailability.EMPTY);
            }
        }
        return seatAvailabilities;
    }
}