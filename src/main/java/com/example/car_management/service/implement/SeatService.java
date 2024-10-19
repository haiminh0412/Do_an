package com.example.car_management.service.implement;

import com.example.car_management.dto.CarDTO;
import com.example.car_management.dto.SeatAvailability;
import com.example.car_management.dto.SeatDTO;
import com.example.car_management.entity.Car;
import com.example.car_management.entity.Seat;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.model.Position;
import com.example.car_management.repository.ICarRepository;
import com.example.car_management.repository.ISeatRepository;
import com.example.car_management.service.Interface.ISeatService;
import com.example.car_management.utils.StringUtils;
import com.google.firebase.database.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SeatService implements ISeatService {
    private static final int EXSITS = 1;

    @Autowired
    ISeatRepository seatRepository;

    @Autowired
    ICarRepository carRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private CarService carService;

    @Autowired
    private TripDetailService tripDetailService;

    private final SimpMessagingTemplate messagingTemplate;

    // Sử dụng ConcurrentHashMap để lưu trữ thông tin giữ ghế và trạng thái ghế
    private final Map<String, String> seatCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> sessionCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> expirationCache = new ConcurrentHashMap<>();

    private static final String SEAT_PREFIX = "SEAT_";
    private static final String SESSION_PREFIX = "SESSION_";
    private static final int MAX_HOLD_SEATS = 4;
    private static final int HOLD_EXPIRATION_MINUTES = 1;

    @Autowired
    public SeatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Phương thức để giữ chỗ
    public boolean holdSeat(String sessionId, Integer seatId, Integer tripDetailId, String departureDate) {
        String seatKey = generateSeatKey(seatId, tripDetailId, departureDate);
        String sessionKey = generateSessionKey(sessionId);

        // Kiểm tra số lượng ghế đã giữ của session
        Integer seatsHeld = sessionCache.getOrDefault(sessionKey, 0);

        // Nếu session đã giữ quá 4 ghế, không cho phép giữ thêm
        if (seatsHeld >= MAX_HOLD_SEATS) {
            return false;
        }

        // Kiểm tra xem ghế đã bị giữ hay chưa
        if (seatCache.containsKey(seatKey)) {
            return false; // Ghế đã được giữ
        }

        // Tính thời gian hết hạn
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(HOLD_EXPIRATION_MINUTES);
        expirationCache.put(seatKey, expirationTime);

        // Giữ ghế trong bộ nhớ cache và tăng số lượng ghế đã giữ của session
        seatCache.put(seatKey, "HELD");
        sessionCache.put(sessionKey, seatsHeld + 1);

        // Gửi thông báo giữ ghế tới các người dùng
        messagingTemplate.convertAndSend("/topic/hold", seatKey);
        return true;
    }

    // Phương thức để hủy chỗ
    public boolean cancelSeat(String sessionId, Integer seatId, Integer tripDetailId, String departureDate) {
        String seatKey = generateSeatKey(seatId, tripDetailId, departureDate);
        String sessionKey = generateSessionKey(sessionId);

        // Kiểm tra xem ghế có bị giữ hay không
        if (!seatCache.containsKey(seatKey)) {
            return false; // Ghế không bị giữ
        }

        // Hủy giữ ghế và giảm số lượng ghế đã giữ của session
        seatCache.remove(seatKey);
        expirationCache.remove(seatKey);

        Integer seatsHeld = sessionCache.getOrDefault(sessionKey, 0);
        if (seatsHeld > 0) {
            sessionCache.put(sessionKey, seatsHeld - 1);
        }

        // Gửi thông báo hủy ghế tới các người dùng
        messagingTemplate.convertAndSend("/topic/cancel", seatKey);
        return true;
    }

    // Phương thức để kiểm tra và tự động hủy chỗ quá hạn
    public void autoReleaseExpiredSeats() {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, LocalDateTime> entry : expirationCache.entrySet()) {
            if (entry.getValue().isBefore(now)) {
                cancelSeatFromKey(entry.getKey()); // Hủy chỗ nếu quá hạn
            }
        }
    }

    private void cancelSeatFromKey(String seatKey) {
        String sessionKey = seatKey.replace(SEAT_PREFIX, SESSION_PREFIX);
        seatCache.remove(seatKey);
        expirationCache.remove(seatKey);

        Integer seatsHeld = sessionCache.getOrDefault(sessionKey, 0);
        if (seatsHeld > 0) {
            sessionCache.put(sessionKey, seatsHeld - 1);
        }

        // Gửi thông báo hủy ghế tới các người dùng
        messagingTemplate.convertAndSend("/topic/cancel", seatKey);
    }

    // Tạo key cho bộ nhớ cache với thông tin về ghế
    private String generateSeatKey(Integer seatId, Integer tripDetailId, String departureDate) {
        return SEAT_PREFIX + seatId + "_" + tripDetailId + "_" + departureDate;
    }

    // Tạo key cho bộ nhớ cache với thông tin về session
    private String generateSessionKey(String sessionId) {
        return SESSION_PREFIX + sessionId;
    }
//
//    // Phương thức để giữ ghế
    @Transactional
    public SeatAvailability holdSeat(int seatId, int tripDetailId, LocalDate departureDate, int holdDurationMinutes) {
//        String key = generateKey(seatId, tripDetailId, departureDate);
//        SeatAvailability seatAvailability = seatAvailabilityMap.computeIfAbsent(key, k -> new SeatAvailability());
//        seatAvailability.holdSeat(holdDurationMinutes);
//
//        // Gửi thông báo cập nhật trạng thái ghế tới tất cả người dùng
//        messagingTemplate.convertAndSend("/topic/seatStatus/" + seatId + "/" + tripDetailId + "/" + departureDate, seatAvailability);

        SeatAvailability seatAvailability = SeatAvailability.builder()
                .seat(this.findById(seatId))
                .tripDetail(tripDetailService.findById(tripDetailId))
                .departureDate(departureDate)
                .status(SeatAvailability.HOLD)
                .build();

//        seatAvailability.holdSeat(holdDurationMinutes);

        return seatAvailability;
    }

    @Transactional
    public SeatAvailability cancelSeat(int seatId, int tripDetailId, LocalDate departureDate) {
//        String key = generateKey(seatId, tripDetailId, departureDate);
//        SeatAvailability seatAvailability = seatAvailabilityMap.computeIfAbsent(key, k -> new SeatAvailability());
//        seatAvailability.holdSeat(holdDurationMinutes);
//
//        // Gửi thông báo cập nhật trạng thái ghế tới tất cả người dùng
//        messagingTemplate.convertAndSend("/topic/seatStatus/" + seatId + "/" + tripDetailId + "/" + departureDate, seatAvailability);

        SeatAvailability seatAvailability = SeatAvailability.builder()
                .seat(this.findById(seatId))
                .tripDetail(tripDetailService.findById(tripDetailId))
                .departureDate(departureDate)
                .status(SeatAvailability.EMPTY)
                .build();

        return seatAvailability;
    }
//
//    // Phương thức để đặt ghế
//    public SeatAvailability bookSeat(int seatId, int tripDetailId, LocalDate departureDate) {
//        String key = generateKey(seatId, tripDetailId, departureDate);
//        SeatAvailability seatAvailability = seatAvailabilityMap.get(key);
//
//        if (seatAvailability != null && seatAvailability.isHoldExpired()) {
//            seatAvailability.bookSeat();
//
//            // Gửi thông báo cập nhật trạng thái ghế tới tất cả người dùng
//            messagingTemplate.convertAndSend("/topic/seatStatus/" + seatId + "/" + tripDetailId + "/" + departureDate, seatAvailability);
//        }
//
//        return seatAvailability;
//    }
//
//    // Phương thức để giải phóng ghế đã giữ
//    public SeatAvailability releaseHold(int seatId, int tripDetailId, LocalDate departureDate) {
//        String key = generateKey(seatId, tripDetailId, departureDate);
//        SeatAvailability seatAvailability = seatAvailabilityMap.get(key);
//
//        if (seatAvailability != null) {
//            seatAvailability.releaseHold();
//
//            // Gửi thông báo cập nhật trạng thái ghế tới tất cả người dùng
//            messagingTemplate.convertAndSend("/topic/seatStatus/" + seatId + "/" + tripDetailId + "/" + departureDate, seatAvailability);
//        }
//
//        return seatAvailability;
//    }
//
//    // Phương thức để tạo khóa duy nhất cho ghế
    private String generateKey(int seatId, int tripDetailId, LocalDate departureDate) {
        return seatId + "_" + tripDetailId + "_" + departureDate.toString();
    }

//    // Phương thức để giữ ghế
//    public CompletableFuture<Void> holdSeat(String seatId, String tripDetailId, LocalDate departureDate, int holdDurationMinutes) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        long expiryTime = LocalDateTime.now().plusMinutes(holdDurationMinutes).toEpochSecond(java.time.ZoneOffset.UTC);
//
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("status", SeatAvailability.HOLD);
//        updates.put("tripDetailId", tripDetailId);
//        updates.put("departureDate", departureDate.toString());
//        updates.put("holdExpiryTime", expiryTime);
//
//        seatRef.child(seatId).child(tripDetailId).child(departureDate.toString()).updateChildren(updates, (error, ref) -> {
//            if (error != null) {
//                future.completeExceptionally(error.toException());
//            } else {
//                future.complete(null);
//            }
//        });
//
//        return future;
//    }
//
//    // Phương thức để kiểm tra xem thời gian giữ ghế đã hết hạn chưa
//    public CompletableFuture<Boolean> isHoldExpired(String seatId, String tripDetailId, LocalDate departureDate) {
//        CompletableFuture<Boolean> future = new CompletableFuture<>();
//        seatRef.child(seatId).child(tripDetailId).child(departureDate.toString()).child("holdExpiryTime")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        Long expiryTime = snapshot.getValue(Long.class);
//                        if (expiryTime == null) {
//                            future.complete(false);
//                        } else {
//                            boolean isExpired = LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.UTC) > expiryTime;
//                            future.complete(isExpired);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        future.completeExceptionally(error.toException());
//                    }
//                });
//
//        return future;
//    }
//
//    // Phương thức để hủy giữ ghế
//    public CompletableFuture<Void> releaseHold(String seatId, String tripDetailId, LocalDate departureDate) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("status", SeatAvailability.EMPTY);
//        updates.remove("holdExpiryTime");
//        updates.remove("tripDetailId");
//        updates.remove("departureDate");
//
//        seatRef.child(seatId).child(tripDetailId).child(departureDate.toString()).updateChildren(updates, (error, ref) -> {
//            if (error != null) {
//                future.completeExceptionally(error.toException());
//            } else {
//                future.complete(null);
//            }
//        });
//
//        return future;
//    }
//
//    // Phương thức để đặt ghế
//    public CompletableFuture<Void> bookSeat(String seatId, String tripDetailId, LocalDate departureDate) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("status", SeatAvailability.BOOKED);
//        updates.remove("holdExpiryTime");
//        updates.remove("tripDetailId");
//        updates.remove("departureDate");
//
//        seatRef.child(seatId).child(tripDetailId).child(departureDate.toString()).updateChildren(updates, (error, ref) -> {
//            if (error != null) {
//                future.completeExceptionally(error.toException());
//            } else {
//                future.complete(null);
//            }
//        });
//
//        return future;
//    }
//
//    // Phương thức để cập nhật trạng thái ghế
//    public CompletableFuture<Void> updateSeatStatus(String seatId, String tripDetailId, LocalDate departureDate, int status) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("status", status);
//
//        seatRef.child(seatId).child(tripDetailId).child(departureDate.toString()).updateChildren(updates, (error, ref) -> {
//            if (error != null) {
//                future.completeExceptionally(error.toException());
//            } else {
//                future.complete(null);
//            }
//        });
//
//        return future;
//    }

    @Override
    public SeatDTO insert(SeatDTO seatRequest) {
        String seatNumber = StringUtils.normalizeString(seatRequest.getSeatNumber().trim());
        seatRequest.setSeatNumber(seatNumber);

        if(seatRepository.existsBySeatNumber(seatRequest.getCar().getCarId(), seatRequest.getSeatNumber().trim()) == EXSITS) {
            throw new AppException(ErrorCode.EXIST);
        }

        Seat seat = modelMapper.map(seatRequest, Seat.class);
        Car car = carRepository.findById(seatRequest.getCar().getCarId()).get();
        car.addSeat(seat);
        Seat newSeat = seatRepository.save(seat);
        SeatDTO seatResponse = modelMapper.map(newSeat, SeatDTO.class);
        seatResponse.setCar(modelMapper.map(car, CarDTO.class));
        return seatResponse;
    }

    @Override
    public SeatDTO update(SeatDTO seatRequest, Integer id) {
        if(!seatRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }

        String seatNumber = StringUtils.normalizeString(seatRequest.getSeatNumber().trim());
        seatRequest.setSeatNumber(seatNumber);

        if(seatRepository.existsBySeatNumber(seatRequest.getCar().getCarId(), seatRequest.getSeatNumber().trim(),
                id) == EXSITS) {
            throw new AppException(ErrorCode.EXIST);
        }

        Seat seat = seatRepository.findById(id).get();
        Car car = carRepository.findById(seatRequest.getCar().getCarId()).get();
        car.addSeat(seat);

        seat.setSeatNumber(seatRequest.getSeatNumber().trim());
        seat.setX(seatRequest.getX());
        seat.setY(seatRequest.getY());

        Seat updatedSeat = seatRepository.save(seat);
        SeatDTO seatResponse = modelMapper.map(updatedSeat, SeatDTO.class);
        seatResponse.setCar(modelMapper.map(seat, CarDTO.class));
        return seatResponse;
    }

    @Override
    public void deleteById(Integer id) {
        if(!seatRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }
        seatRepository.deleteById(id);
    }

    @Override
    public SeatDTO findById(Integer id) {
        if(!seatRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }

        Seat seat = seatRepository.findById(id).get();
        SeatDTO seatResponse = modelMapper.map(seat, SeatDTO.class);

        Car car = carRepository.findById(seat.getCar().getCarId()).get();
        seatResponse.setCar(modelMapper.map(car, CarDTO.class));

        return seatResponse;
    }

    @Override
    public List<SeatDTO> findAll() {
        List<SeatDTO> seats = seatRepository.findAll().stream()
                .map(seat -> {
                    SeatDTO seatResponse = modelMapper.map(seat, SeatDTO.class);

                    CarDTO carResponse = modelMapper.map(seat.getCar(), CarDTO.class);
                    seatResponse.setCar(carResponse);

                    return seatResponse;
                }).collect(Collectors.toList());
        return seats;
    }

    public List<SeatDTO> findAllSeatByCar(Integer carId) {
        List<SeatDTO> seats = seatRepository.findAllSeatIdByCar(carId).stream()
                .map(seat -> {
                    SeatDTO seatResponse = modelMapper.map(seat, SeatDTO.class);

                    CarDTO carResponse = modelMapper.map(seat.getCar(), CarDTO.class);
                    seatResponse.setCar(carResponse);

                    return seatResponse;
                }).collect(Collectors.toList());
        return seats;
    }

    public SeatDTO findSeatByName(String seatNumber) {
        Seat seat = seatRepository.findSeatByseatNumber(seatNumber);
        if(seat == null)
            throw new AppException(ErrorCode.NOT_EXIST);

        SeatDTO seatResponse = modelMapper.map(seat, SeatDTO.class);
        return seatResponse;
    }

    public String[][] createSeatsMap(Integer carId, String[][] seatMaps) {
        CarDTO car = carService.findById(carId);

        for(int i = 0; i < seatMaps.length; ++i) {
            for(int j = 0; j < seatMaps[i].length; ++j) {
                if(seatMaps[i][j] != null && !seatMaps[i][j].isEmpty()) {
                    SeatDTO seatDTO = new SeatDTO().builder()
                            .seatNumber(seatMaps[i][j])
                            .x(i)
                            .y(j)
                            .car(car)
                            .build();
                    this.insert(seatDTO);
                }
            }
        }
        return this.getAllSeatsMapByCarId(carId);
    }

    public String[][] getAllSeatsMapByCarId(Integer carId) {
        List<SeatDTO> seats = this.findAllSeatByCar(carId);

        int row = 0;
        int col = 0;
        Set<Integer> setX = new HashSet<>();
        Set<Integer> setY = new HashSet<>();
        Map<Position, String> map = new HashMap<>();

        for(int i = 0; i < seats.size(); ++i) {
            SeatDTO seatDTO = seats.get(i);

            if(seatDTO.getX() == null || seatDTO.getY() == null)
                return new String[][] {};

            row = Math.max(row, seatDTO.getX());
            col = Math.max(col, seatDTO.getY());
            setX.add(seatDTO.getX());
            setY.add(seatDTO.getY());

            Position position = new Position(seatDTO.getX(), seatDTO.getY());
            map.put(position, seatDTO.getSeatNumber());
        }

        String[][] seatsMap = new String[row + 1][col + 1];
        for(int i = 0; i < seatsMap.length; ++i) {
            for(int j = 0; j < seatsMap[i].length; ++j) {
                Position position = new Position(i, j);
                seatsMap[i][j] = map.getOrDefault(position, "");
            }
        }
        return seatsMap;
    }

    public List<String[][]> getAllSeatsMap() {
        List<CarDTO> cars = carService.findAll();
        List<String[][]> seatsMap = new ArrayList<>();
        for(CarDTO car : cars) {
            String[][] seatMap = this.getAllSeatsMapByCarId(car.getCarId());
            seatsMap.add(seatMap);
        }
        return seatsMap;
    }
}
