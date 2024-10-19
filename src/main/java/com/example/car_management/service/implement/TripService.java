package com.example.car_management.service.implement;

import com.example.car_management.dto.CarDTO;
import com.example.car_management.dto.response.DepartureResponse;
import com.example.car_management.dto.response.DestiantionResponse;
import com.example.car_management.dto.TripDTO;
import com.example.car_management.entity.Car;
import com.example.car_management.entity.Trip;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.pagination.response.CarPageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.pagination.response.TripPageResponse;
import com.example.car_management.repository.ITripRepository;
import com.example.car_management.service.Interface.ITripService;
import com.example.car_management.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService implements ITripService {
    @Autowired
    private final ITripRepository tripRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public TripDTO insert(final TripDTO tripRequest) {
        if(tripRepository.existsByDepartureAndDestination(tripRequest.getDeparture(), tripRequest.getDestination())) {
            throw new AppException(ErrorCode.EXIST);
        }

        // convert DTO to entity
        Trip trip = modelMapper.map(tripRequest, Trip.class);

        Trip newTrip =  tripRepository.save(trip);

        // convert entity to DTO
        TripDTO tripResponse = modelMapper.map(newTrip, TripDTO.class);

        return tripResponse;
    }

    @Override
    public TripDTO update(final TripDTO tripRequest, final Integer id) {
        if(!tripRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }

        if(tripRepository.existsByDepartureAndDestinationAndTripIdNot(tripRequest.getDeparture(), tripRequest.getDestination(), id)) {
            throw new AppException(ErrorCode.EXIST);
        }

        Trip trip = modelMapper.map(this.findById(id), Trip.class);
        trip.setDeparture(tripRequest.getDeparture().trim());
        trip.setDestination(tripRequest.getDestination().trim());

        Trip updatedTrip = tripRepository.save(trip);
        TripDTO tripResponse = modelMapper.map(updatedTrip, TripDTO.class);
        return tripResponse;
    }

    @Override
    public void deleteById(final Integer id) {
        if(!tripRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }

        tripRepository.deleteById(id);
    }

    @Override
    public TripDTO findById(final Integer id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
        TripDTO tripResponse = modelMapper.map(trip, TripDTO.class);
        return tripResponse;
    }

    @Override
    public List<TripDTO> findAll() {
        return this.convertToTripDTO(tripRepository.findAll());
    }

    public List<DepartureResponse> getAllDeparture() {
        List<DepartureResponse> departures = new ArrayList<>();
        for(String departure : tripRepository.getAllDeparture()) {
            departures.add(new DepartureResponse(departure));
        }
        return departures;
    }

    public List<DestiantionResponse> getAllDestination() {
        List<DestiantionResponse> destinations = new ArrayList<>();
        for(String destination : tripRepository.getAllDestination()) {
            destinations.add(new DestiantionResponse(destination));
        }
        return destinations;
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

        Page<Trip> trips = tripRepository.findAll(pageable);

        List<TripDTO> tripsResponse = convertToTripDTO(trips);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(trips.getTotalPages())
                .totalElements(trips.getTotalElements())
                .items(tripsResponse)
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

        Page<Trip> trips = tripRepository.findAll(pageable);

        List<TripDTO> tripsResponse = convertToTripDTO(trips);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(trips.getTotalPages())
                .totalElements(trips.getTotalElements())
                .items(tripsResponse)
                .build();
    }

    public PageResponse<?> findAllPagination(final Integer pageSize, final Integer pageNo) {
        return this.findAllPaginationWithSortBy(pageSize, pageNo, null);
    }

    public List<TripDTO> convertToTripDTO(final List<Trip> trips) {
        return trips.stream()
                .map(trip -> modelMapper.map(trip, TripDTO.class)).collect(Collectors.toList());
    }

    public List<TripDTO> convertToTripDTO(final Page<Trip> trips) {
        return trips.stream()
                .map(trip -> modelMapper.map(trip, TripDTO.class)).collect(Collectors.toList());
    }

    public List<String> findAllTrips() {
        return tripRepository.findAllTripsWithFormat();
    }

    public TripPageResponse findAllPaginationWithSearch(Integer pageSize, Integer pageNo, String departure, String destination) {
        // Tạo đối tượng phân trang
        TripPageResponse tripPageResponse = new TripPageResponse(pageSize, pageNo * pageSize);

        // Tìm kiếm theo tên và phân trang
        Page<Trip> tripPageResult = tripRepository.searchTrips(departure, destination, tripPageResponse);

        // Chuyển đổi kết quả thành DTO
        List<TripDTO> trips = tripPageResult.hasContent()
                ? this.convertToTripDTO(tripPageResult.getContent())
                : new ArrayList<>();

        // Tạo đối tượng phản hồi và thiết lập dữ liệu
        tripPageResponse.setTrips(trips);
        tripPageResponse.setTotalElements(findAllTrip(departure, destination).size());

        return tripPageResponse;
    }


    public List<TripDTO> findAllTrip(String departure, String destination) {
        // Tim kiem theo ten loai xe
        List<Trip> tripsSearch = tripRepository.searchTrips(departure, destination);
        return convertToTripDTO(tripsSearch);
    }
}