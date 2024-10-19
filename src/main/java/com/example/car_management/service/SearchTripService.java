package com.example.car_management.service;

import com.example.car_management.dto.FilteredTripDetailDTO;
import com.example.car_management.dto.TripDetailDTO;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.ICarRepository;
import com.example.car_management.repository.ITripDetailRepository;
import com.example.car_management.repository.ITripRepository;
import com.example.car_management.service.implement.TripDetailService;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SearchTripService {
    @Autowired
    ITripDetailRepository tripDetailRepository;

    @Autowired
    TripDetailService tripDetailService;

    @Autowired
    ITripRepository tripRepository;

    @Autowired
    ICarRepository carRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private EntityManager entityManager;

//    public List<TripDetailResponse>  findTripDetailsByCondition(String departure, String destination, LocalDate departureDate) {
//        // Truoc tien kiem tra xem ngay di co truoc ngay hien tai khong
//        if(departureDate.isBefore(LocalDate.now())) {
//            throw new AppException(ErrorCode.BEFORE_DATE_NOW);
//        }
//
//        List<TripDetailResponse> tripDetails = new ArrayList<>();
//        try {
//            if(entityManager != null) {
//                entityManager.getTransaction().begin();
//
//                CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//                CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
//
//                Root<TripDetail> tripDetailRoot = criteriaQuery.from(TripDetail.class); // SELECT * FROM TRIP_DETAIL
//                Join<TripDetail, Car> carJoin = tripDetailRoot.join(TripDetail.CAR, JoinType.INNER); // SELECT * FROM TRIP_DETAIL INNER JOIN CAR
//                Join<TripDetail, Trip> tripJoin = tripDetailRoot.join(TripDetail.TRIP, JoinType.INNER);
//                Join<Car, CarType> carTypeJoin = carJoin.join(Car.CAR_TYPE, JoinType.INNER);
//
//                criteriaQuery.multiselect(tripDetailRoot, carJoin, tripJoin);
//
//                // Tạo điều kiện mặc định (tương đương với WHERE 1=1)
//                Predicate greaterThanDateTimeCondition = criteriaBuilder.conjunction();
//
//                // Kiem tra xem ngay di co phai la ngay hom nay khong
//                if(departureDate.equals(LocalDate.now())) {
//                    // Lọc ra những chuyến chưa đến giờ chạy so với ngày hiện tại
//                    LocalTime currentTime = LocalTime.now();
//                    greaterThanDateTimeCondition = criteriaBuilder.greaterThan(tripDetailRoot.get(TripDetail.DEPARTURE_TIME), currentTime);
//                }
//                // Cập nhật tất cả điều kiện
//                criteriaQuery.where(greaterThanDateTimeCondition);
//                // sap xep theo gio xuat phat theo thu tu tang dan
//                criteriaQuery.orderBy(criteriaBuilder.asc(tripDetailRoot.get(TripDetail.DEPARTURE_TIME)));
//
//                TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
//                List<Tuple> results = query.getResultList();
//
//                // Ánh xạ kết quả sang TripDetailDTO và thêm vào danh sách
//                for (Tuple result : results) {
//                    TripDetail tripDetail = result.get(tripDetailRoot);
//                    TripDetailResponse tripDetailResponse = modelMapper.map(tripDetail, TripDetailResponse.class);
//
//                    Car car = result.get(carJoin);
//                    CarResponse carResponse = modelMapper.map(car, CarResponse.class);
//                    tripDetailResponse.setCar(carResponse);
//
//                    Trip trip = result.get(tripJoin);
//                    TripResposne tripResposne  = modelMapper.map(trip, TripResposne.class);
//                    tripDetailResponse.setTrip(tripResposne);
//
//                    // Thêm vào danh sách
//                    tripDetails.add(tripDetailResponse);
//                }
//
//                entityManager.getTransaction().commit(); // end of transaction
//            }
//        } catch (Exception e) {
//            System.out.println("Lỗi: " + e.getMessage());
//        } finally {
//            if(entityManager != null && entityManager.getTransaction().isActive()) {
//                entityManager.close();
//            }
//        }
//        return tripDetails;
//    }

    public Integer countAvailableSeats(TripDetailDTO tripDetailDTO, LocalDate departureDate) {
        Integer countSeatsBooked = tripDetailRepository.countBookedSeats(tripDetailDTO.getTripDetailId(), departureDate);
        Integer countSeastAvailable = tripDetailDTO.getCar().getNumberOfSeats() - countSeatsBooked;
        return countSeastAvailable > 0 ? countSeastAvailable : 0;
    }

    // Phương thức sắp xếp theo thời gian chạy tăng dần
    private void sortTripDetailsByDepartureTime(List<FilteredTripDetailDTO> tripDetails) {
        Collections.sort(tripDetails, new Comparator<FilteredTripDetailDTO>() {
            @Override
            public int compare(FilteredTripDetailDTO o1, FilteredTripDetailDTO o2) {
                if(o1.getTripDetail().getDepartureTime().equals(o2.getTripDetail().getDepartureTime()))
                    return o1.getTripDetail().getDestinationTime().compareTo(o2.getTripDetail().getDestinationTime());
                return o1.getTripDetail().getDepartureTime().compareTo(o2.getTripDetail().getDepartureTime());
            }
        });
    }

    public List<FilteredTripDetailDTO> findTripDetailsByCondition(String departure, String destination, LocalDate departureDate) {
        if (departureDate == null)
            throw new AppException(ErrorCode.NULL);

        if (departureDate.isBefore(LocalDate.now()))
            throw new AppException(ErrorCode.BEFORE_DATE_NOW);

        List<FilteredTripDetailDTO> filteredTripDetails = new ArrayList<>();
        for (TripDetailDTO tripDetail : tripDetailService.findAll()) {
            if (isValidDepartureDateTime(tripDetail.getDepartureTime(), departureDate)
                    && isEqualsDeparture(tripDetail.getTrip().getDeparture(), departure)
                    && isEqualsDestination(tripDetail.getTrip().getDestination(), destination)) {
                // them cac tripdetail thoa man dieu kien vao danh sach
                Integer availableSeats = countAvailableSeats(tripDetail, departureDate);
                FilteredTripDetailDTO filteredTripDetailsDTO = new FilteredTripDetailDTO().builder()
                        .tripDetail(tripDetail)
                        .availableSeats(availableSeats)
                        .build();
                filteredTripDetails.add(filteredTripDetailsDTO);
            }
        }

        // Sắp xếp danh sách tripDetails theo thời gian chạy tăng dần
        sortTripDetailsByDepartureTime(filteredTripDetails);

        return filteredTripDetails;
    }

    public List<FilteredTripDetailDTO> findTripDetailsByCondition(String departure, String destination, LocalDate departureDate,
                                                          String carTypeName, Long minPrice, Long maxPrice, LocalTime startTime, LocalTime endTime) {
        if (departureDate == null)
            throw new AppException(ErrorCode.NULL);

        if (departureDate.isBefore(LocalDate.now()))
            throw new AppException(ErrorCode.BEFORE_DATE_NOW);

        List<FilteredTripDetailDTO> filteredTripDetails = new ArrayList<>();
        for (TripDetailDTO tripDetail : tripDetailService.findAll()) {
            if (isValidDepartureDateTime(tripDetail.getDepartureTime(), departureDate)
                    && isEqualsDeparture(tripDetail.getTrip().getDeparture(), departure)
                    && isEqualsDestination(tripDetail.getTrip().getDestination(), destination)
                    && isEqualsCarType(tripDetail.getCar().getCarType().getName(), carTypeName)
                    && inPriceRange(tripDetail.getPrice(), minPrice, maxPrice)
                    && isDepartureTimeWithinRange(tripDetail.getDepartureTime(), startTime, endTime)) {
                // them cac tripdetail thoa man dieu kien vao danh sach
                Integer availableSeats = countAvailableSeats(tripDetail, departureDate);
                FilteredTripDetailDTO filteredTripDetailsDTO = new FilteredTripDetailDTO().builder()
                        .tripDetail(tripDetail)
                        .availableSeats(availableSeats)
                        .build();
                filteredTripDetails.add(filteredTripDetailsDTO);
            }
        }

        // Sắp xếp theo thoi gian chay tang dan
        sortTripDetailsByDepartureTime(filteredTripDetails);

        return filteredTripDetails;
    }

    public Page<?> findTripDetailsByCondition(String departure, String destination, LocalDate departureDate, Pageable pageable) {
        if (departureDate == null) {
            throw new AppException(ErrorCode.NULL);
        }

        if (departureDate.isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.BEFORE_DATE_NOW);
        }

        List<FilteredTripDetailDTO> filteredTripDetails = new ArrayList<>();

        // Lấy danh sách trip detail
        List<TripDetailDTO> allTripDetails = tripDetailService.findAll();

        // Lọc trip detail theo điều kiện
        for (TripDetailDTO tripDetail : allTripDetails) {
            if (isValidDepartureDateTime(tripDetail.getDepartureTime(), departureDate)
                    && isEqualsDeparture(tripDetail.getTrip().getDeparture(), departure)
                    && isEqualsDestination(tripDetail.getTrip().getDestination(), destination)) {
                // Thêm các trip detail thỏa mãn điều kiện vào danh sách
                Integer availableSeats = countAvailableSeats(tripDetail, departureDate);
                FilteredTripDetailDTO filteredTripDetailsDTO = new FilteredTripDetailDTO().builder()
                        .tripDetail(tripDetail)
                        .availableSeats(availableSeats)
                        .build();
                filteredTripDetails.add(filteredTripDetailsDTO);
            }
        }

        // Sắp xếp danh sách tripDetails theo thời gian chạy tăng dần
        sortTripDetailsByDepartureTime(filteredTripDetails);

        // Tính toán chỉ số bắt đầu và kết thúc cho phân trang
        int totalSize = filteredTripDetails.size();
        int fromIndex = Math.min(pageable.getPageNumber() * pageable.getPageSize(), totalSize);
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), totalSize);

        // Tạo trang dữ liệu
        List<FilteredTripDetailDTO> pagedContent = filteredTripDetails.subList(fromIndex, toIndex);
        return new PageImpl<>(pagedContent, pageable, totalSize);
    }

    public boolean isDepartureTimeWithinRange(LocalTime departureTime, LocalTime startTime, LocalTime endTime) {
        // Kiểm tra nếu khoảng thời gian kéo dài qua đêm
        if (startTime.isAfter(endTime)) {
            // Trường hợp: startTime > endTime (ví dụ: 18:00 đến 4:59)
            return departureTime.isAfter(startTime) || departureTime.isBefore(endTime);
        } else {
            // Trường hợp bình thường
            return (departureTime.isAfter(startTime) || departureTime.equals(startTime)) &&
                    (departureTime.isBefore(endTime) || departureTime.equals(endTime));
        }
    }


    public boolean isValidDepartureDateTime(LocalTime departureTime, LocalDate departureDate) {
        if (departureDate.isBefore(LocalDate.now()))
            throw new AppException(ErrorCode.BEFORE_DATE_NOW);

        if (departureDate.equals(LocalDate.now())) {
            return departureTime.isAfter(LocalTime.now());
        }
        return true;
    }

    public boolean isEqualsDeparture(String departure, String departureRequest) {
        if (departureRequest == null)
            throw new AppException(ErrorCode.NULL);

        return departure.equalsIgnoreCase(departureRequest);
    }

    public boolean isEqualsDestination(String destination, String destinationRequest) {
        if (destinationRequest == null)
            throw new AppException(ErrorCode.NULL);

        return destination.equalsIgnoreCase(destinationRequest);
    }

    public boolean isEqualsCarType(String carTypeName, String carTypeNameRequest) {
        if (carTypeNameRequest == null || carTypeNameRequest.equalsIgnoreCase("All"))
            return true;

        return carTypeName.equalsIgnoreCase(carTypeNameRequest);
    }

    public boolean inPriceRange(Long price, Long minPrice, Long maxPrice) {
        if ((minPrice == null || minPrice < 0) || (maxPrice == null || maxPrice < 0))
            return true;

        return price >= minPrice && price <= maxPrice;
    }
}