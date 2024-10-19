package com.example.car_management.service.implement;

import com.example.car_management.dto.CarDTO;
import com.example.car_management.dto.request.SearchTripRequest;
import com.example.car_management.dto.response.CarResponse;
import com.example.car_management.dto.response.TripDetailResponse;
import com.example.car_management.dto.response.TripResponse;
import com.example.car_management.dto.TripDTO;
import com.example.car_management.dto.TripDetailDTO;
import com.example.car_management.entity.*;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.pagination.response.CarPageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.pagination.response.TripDetailPageResponse;
import com.example.car_management.repository.ITripDetailRepository;
import com.example.car_management.service.Interface.ITripDetailService;
import com.example.car_management.utils.StringUtils;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TripDetailService implements ITripDetailService {
    private static final int EXISTS = 1;
    private static final String ALL_CAR_TYPE = "ALL";

    @Autowired
    private final ITripDetailRepository tripDetailRepository;

    @Autowired
    private final TripService tripService;

    @Autowired
    private final CarService carService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityManager entityManager;

    @Override
    public TripDetailDTO insert( TripDetailDTO tripDetailRequest) {
        Trip trip = modelMapper.map(tripService.findById(tripDetailRequest.getTrip().getTripId()), Trip.class);

        Car car = modelMapper.map(carService.findById(tripDetailRequest.getCar().getCarId()), Car.class);

       TripDetail tripDetail = modelMapper.map(tripDetailRequest, TripDetail.class);

        if(tripDetailRepository.existsByCarAndTrip(car, trip)) {
            throw new AppException(ErrorCode.EXIST);
        }

        // Kiểm tra nếu xe đã có lịch trình trùng thời gian
        List<TripDetail> overlappingTrips = tripDetailRepository.findOverlappingTrips(
                car.getCarId(),
                tripDetailRequest.getDepartureTime(),
                tripDetailRequest.getDestinationTime()
        );
        if (!overlappingTrips.isEmpty()) {
            throw new AppException(ErrorCode.OVERLAPPING_SCHEDULE);
        }

        if(tripDetailRequest.getPrice() < 0)
            throw new AppException(ErrorCode.INVALID_PRICE);

        trip.addTripDetail(tripDetail);
        car.addTripDetail(tripDetail);

        TripDetail newTripDetail = tripDetailRepository.save(tripDetail);

        TripDetailDTO tripDetaiResponse =  modelMapper.map(newTripDetail, TripDetailDTO.class);
        tripDetaiResponse.setTrip(modelMapper.map(trip, TripDTO.class));
        tripDetaiResponse.setCar(modelMapper.map(car, CarDTO.class));
        return tripDetaiResponse;
    }

    @Override
    public TripDetailDTO update(final TripDetailDTO tripDetailRequest, final Integer id) {
        if(!tripDetailRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }

//        if(tripDetailRepository.existsByCarAndTripAndTripDetailIdNot(tripDetailRequest.getCar(),
//                tripDetailRequest.getTrip(), id)) {
//            throw new AppException(ErrorCode.EXIST);
//        }

        if(tripDetailRequest.getPrice() < 0)
            throw new AppException(ErrorCode.INVALID_PRICE);

        Car car = modelMapper.map(carService.findById(tripDetailRequest.getCar().getCarId()), Car.class);
        // Kiểm tra nếu xe đã có lịch trình trùng thời gian
        List<TripDetail> overlappingTrips = tripDetailRepository.findOverlappingTrips(
                car.getCarId(),
                tripDetailRequest.getDepartureTime(),
                tripDetailRequest.getDestinationTime()
        );
        if (!overlappingTrips.isEmpty()) {
            throw new AppException(ErrorCode.OVERLAPPING_SCHEDULE);
        }

        Trip newTrip = modelMapper.map(tripService.findById(tripDetailRequest.getTrip().getTripId()), Trip.class);
        Car newCar = modelMapper.map(carService.findById(tripDetailRequest.getCar().getCarId()), Car.class);
        TripDetail tripDetail = modelMapper.map(this.findById(id), TripDetail.class);

        newTrip.addTripDetail(tripDetail);
        newCar.addTripDetail(tripDetail);

        tripDetail.setPrice(tripDetailRequest.getPrice());
        tripDetail.setDepartureTime(tripDetailRequest.getDepartureTime());
        tripDetail.setDestinationTime(tripDetailRequest.getDestinationTime());

        TripDetail updateTripDetail = tripDetailRepository.save(tripDetail);
        TripDetailDTO tripDetaiResponse = modelMapper.map(updateTripDetail, TripDetailDTO.class);
        tripDetaiResponse.setTrip(modelMapper.map(tripDetail, TripDTO.class));
        tripDetaiResponse.setCar(modelMapper.map(newCar, CarDTO.class));

        return tripDetaiResponse;
    }

    @Override
    public void deleteById(final Integer id) {
        if(!tripDetailRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }
        tripDetailRepository.deleteById(id);
    }

    @Override
    public TripDetailDTO findById(final Integer id) {
        TripDetail tripDetail = tripDetailRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
        TripDetailDTO tripDetailResponse = modelMapper.map(tripDetail, TripDetailDTO.class);

        Trip trip = tripDetail.getTrip();
        tripDetailResponse.setTrip(modelMapper.map(trip, TripDTO.class));

        Car car = tripDetail.getCar();
        tripDetailResponse.setCar(modelMapper.map(car, CarDTO.class));

        return tripDetailResponse;
    }

    @Override
    public List<TripDetailDTO> findAll() {
        return this.convertToTripDetailDTO(tripDetailRepository.findAll());
    }

    public PageResponse<?> findAllPaginationWithSortBy(final Integer pageSize, final Integer pageNo, final String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();

        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(\\w+)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));

        Page<TripDetail> tripDetails = tripDetailRepository.findAll(pageable);

        List<TripDetailDTO> tripDetailsResponse = convertToTripDetailDTO(tripDetails);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(tripDetails.getTotalPages())
                .totalElements(tripDetails.getTotalElements())
                .items(tripDetailsResponse)
                .build();
    }

    public PageResponse<?> findAllPaginationWithSortByMultipleColumns(final Integer pageSize, final Integer pageNo, final String... sorts) {
        List<Sort.Order> orders = new ArrayList<>();

        for (String sortBy: sorts) {
            //firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(\\w+?)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));

        Page<TripDetail> tripDetails = tripDetailRepository.findAll(pageable);

        List<TripDetailDTO> tripDetailsResponse = convertToTripDetailDTO(tripDetails);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(tripDetails.getTotalPages())
                .totalElements(tripDetails.getTotalElements())
                .items(tripDetailsResponse)
                .build();
    }

    public PageResponse<?> findAllPagination(final Integer pageSize, final Integer pageNo) {
        return this.findAllPaginationWithSortBy(pageSize, pageNo, null);
    }

    public List<TripDetailDTO> convertToTripDetailDTO(final List<TripDetail> tripDetails) {
        List<TripDetailDTO> tripDetailsDTO = new ArrayList<>();
        // Ánh xạ kết quả sang TripDetailDTO và thêm vào danh sách
        for (TripDetail tripDetail : tripDetails) {
            TripDetailDTO tripDetailResponse = modelMapper.map(tripDetail, TripDetailDTO.class);

            CarDTO carResponse = modelMapper.map(tripDetail.getCar(), CarDTO.class);
            tripDetailResponse.setCar(carResponse);

            TripDTO tripResposne  = modelMapper.map(tripDetail.getTrip(), TripDTO.class);
            tripDetailResponse.setTrip(tripResposne);

            // Thêm vào danh sách
            tripDetailsDTO.add(tripDetailResponse);
        }
        return tripDetailsDTO;
    }

    public List<TripDetailDTO> convertToTripDetailDTO(final Page<TripDetail> tripDetails) {
        List<TripDetailDTO> tripDetailsDTO = new ArrayList<>();
        // Ánh xạ kết quả sang TripDetailDTO và thêm vào danh sách
        for (TripDetail tripDetail : tripDetails) {
            TripDetailDTO tripDetailResponse = modelMapper.map(tripDetail, TripDetailDTO.class);

            CarDTO carResponse = modelMapper.map(tripDetail.getCar(), CarDTO.class);
            tripDetailResponse.setCar(carResponse);

            TripDTO tripResposne  = modelMapper.map(tripDetail.getTrip(), TripDTO.class);
            tripDetailResponse.setTrip(tripResposne);

            // Thêm vào danh sách
            tripDetailsDTO.add(tripDetailResponse);
        }
        return tripDetailsDTO;
    }

    public List<TripDetailResponse> searchTripDetail(final SearchTripRequest searchTripRequest) {
//        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider()
//                .createContainerEntityManagerFactory(new MyPersistenceUnit(), new HashMap<>());
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<TripDetailResponse> tripDetails = new ArrayList<>();
        try {
            if(entityManager != null) {
                entityManager.getTransaction().begin();

                CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
                CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();

                Root<TripDetail> tripDetailRoot = criteriaQuery.from(TripDetail.class); // SELECT * FROM TRIP_DETAIL
                Join<TripDetail, Car> carJoin = tripDetailRoot.join(TripDetail.CAR, JoinType.INNER); // SELECT * FROM TRIP_DETAIL INNER JOIN CAR
                Join<TripDetail, Trip> tripJoin = tripDetailRoot.join(TripDetail.TRIP, JoinType.INNER);
                Join<Car, CarType> carTypeJoin = carJoin.join(Car.CAR_TYPE, JoinType.INNER);

                criteriaQuery.multiselect(tripDetailRoot, carJoin, tripJoin);

                // Thêm điều kiện where

                // Tìm chuyến xe với điểm đi và điểm đến, không phân biệt chữ hoa chữ thường
                Predicate findDepartureCondition = criteriaBuilder.equal(
                        criteriaBuilder.lower(tripJoin.get(Trip.DEPARTURE)),
                        searchTripRequest.getDeparture().toLowerCase().trim()
                );
                Predicate findDestinationCondition = criteriaBuilder.equal(
                        criteriaBuilder.lower(tripJoin.get(Trip.DESTINATION)),
                        searchTripRequest.getDestination().toLowerCase().trim()
                );
                Predicate searchTripCondition = criteriaBuilder.and(findDepartureCondition, findDestinationCondition);

                // Tìm giá trong khoảng [min, max]
                Predicate greaterThanPriceCondition = criteriaBuilder.greaterThanOrEqualTo(tripDetailRoot.get(TripDetail.PRICE), searchTripRequest.getMinPrice());
                Predicate lessThanPriceCondition = criteriaBuilder.lessThanOrEqualTo(tripDetailRoot.get(TripDetail.PRICE), searchTripRequest.getMaxPrice());
                Predicate findPriceCondition = criteriaBuilder.and(greaterThanPriceCondition, lessThanPriceCondition);

                // Chọn thời gian >= thời gian xuất phát
                Predicate greaterThanTimeCondition = criteriaBuilder.greaterThanOrEqualTo(tripDetailRoot.get(TripDetail.DEPARTURE_TIME), searchTripRequest.getDepartureTime());

                // Lọc ra những chuyến chưa đến giờ chạy so với ngày hiện tại
                LocalDate today = LocalDate.now();
                Predicate greaterThanDateTimeCondition = criteriaBuilder.greaterThanOrEqualTo(tripDetailRoot.get(TripDetail.DEPARTURE_TIME), new Time(0,0,0));
                if (searchTripRequest.getDepartureDate().equals(Date.valueOf(today))) {
                    LocalTime currentTime = LocalTime.now();
                    greaterThanDateTimeCondition = criteriaBuilder.greaterThanOrEqualTo(tripDetailRoot.get(TripDetail.DEPARTURE_TIME), currentTime);
                }

                // Tìm loại xe phù hợp nếu không chọn all (tất cả)
                Predicate findCarTypeCondition = criteriaBuilder.like(carTypeJoin.get(CarType.NAME), "%%");
                if(!searchTripRequest.getCarType().equalsIgnoreCase(ALL_CAR_TYPE)) {
                    findCarTypeCondition = criteriaBuilder.equal(carTypeJoin.get(CarType.NAME), searchTripRequest.getCarType());
                }

                // Cập nhật tất cả điều kiện
                criteriaQuery.where(searchTripCondition, findCarTypeCondition, greaterThanTimeCondition, findPriceCondition, greaterThanDateTimeCondition);
                // sap xep theo gio xuat phat theo thu tu tang dan
                criteriaQuery.orderBy(criteriaBuilder.asc(tripDetailRoot.get(TripDetail.DEPARTURE_TIME)));

                TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
                List<Tuple> results = query.getResultList();


                // Ánh xạ kết quả sang TripDetailDTO và thêm vào danh sách
                for (Tuple result : results) {
                    TripDetail tripDetail = result.get(tripDetailRoot);
                    TripDetailResponse tripDetailResponse = modelMapper.map(tripDetail, TripDetailResponse.class);

                    Car car = result.get(carJoin);
                    CarResponse carResponse = modelMapper.map(car, CarResponse.class);
                    tripDetailResponse.setCar(carResponse);

                    Trip trip = result.get(tripJoin);
                    TripResponse tripResposne  = modelMapper.map(trip, TripResponse.class);
                    tripDetailResponse.setTrip(tripResposne);

                    // Thêm vào danh sách
                    tripDetails.add(tripDetailResponse);
                }

                entityManager.getTransaction().commit(); // end of transaction
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        } finally {
            if(entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.close();
            }
        }
        return tripDetails;
    }

    public TripDetailPageResponse findAllPaginationWithSearch(Integer pageSize, Integer pageNo,
          String departure, String destination, String licensePlate, String carTypeName, Long minPrice, Long maxPrice,
                                                       LocalTime startTime, LocalTime endTime) {
        // Tạo đối tượng phân trang
        TripDetailPageResponse tripDetailPageResponse = new TripDetailPageResponse(pageSize, pageNo * pageSize);

        // Tìm kiếm theo tên và phân trang
        Page<TripDetail> tripDetailPageResult = tripDetailRepository.searchTrips(departure, destination,
                licensePlate,  carTypeName, minPrice, maxPrice, startTime, endTime, tripDetailPageResponse);

        // Chuyển đổi kết quả thành DTO
        List<TripDetailDTO> trips = tripDetailPageResult.hasContent()
                ? this.convertToTripDetailDTO(tripDetailPageResult.getContent())
                : new ArrayList<>();

        // Tạo đối tượng phản hồi và thiết lập dữ liệu
        tripDetailPageResponse.setTripDetails(trips);
        tripDetailPageResponse.setTotalElements(findAllCar(departure, destination,
                licensePlate,  carTypeName, minPrice, maxPrice, startTime, endTime).size());

        return tripDetailPageResponse;
    }


    public List<TripDetailDTO> findAllCar( String departure, String destination, String licensePlate, String carTypeName, Long minPrice, Long maxPrice,
                                    LocalTime startTime, LocalTime endTime) {
        // Tim kiem theo ten loai xe
        List<TripDetail> tripDetails = tripDetailRepository.searchTrips(
                departure, destination,
                licensePlate,  carTypeName, minPrice, maxPrice, startTime, endTime);
        return convertToTripDetailDTO(tripDetails);
    }
}