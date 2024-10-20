package com.example.car_management.service.implement;

import com.example.car_management.dto.CarDTO;
import com.example.car_management.dto.CarTypeDTO;
import com.example.car_management.entity.Car;
import com.example.car_management.entity.CarType;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.pagination.CarSpecification;
import com.example.car_management.pagination.response.CarPageResponse;
import com.example.car_management.pagination.response.CarTypePageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.repository.ICarRepository;
import com.example.car_management.service.Interface.ICarService;
import com.example.car_management.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService implements ICarService {
    @Autowired
    private final ICarRepository carRepository;

    @Autowired
    private final CarTypeService carTypeService;

    @Autowired
    private final ModelMapper modelMapper;

    private boolean isValidNumberOfSeat(Integer seats) {
        return seats >= 4;
    }

    // Biểu thức chính quy để kiểm tra biển số ô tô hợp lệ ở Việt Nam
    private static final String LICENSE_PLATE_REGEX = "^[0-9]{2}[A-Z][0-9]{1}-[0-9]{4,5}$";

    // Phương thức kiểm tra biển số hợp lệ
    public boolean isValidLicensePlate(String licensePlate) {
        // Tạo đối tượng Pattern từ regex
        Pattern pattern = Pattern.compile(LICENSE_PLATE_REGEX);

        // So khớp biển số xe với biểu thức chính quy
        Matcher matcher = pattern.matcher(licensePlate);

        // Trả về true nếu khớp, ngược lại trả về false
        return matcher.matches();
    }

    @Override
    public CarDTO insert(final CarDTO carRequest) {
        if(carRepository.existsBylicensePlate(carRequest.getLicensePlate()))
            throw new AppException(ErrorCode.EXIST);

        if(!isValidNumberOfSeat(carRequest.getNumberOfSeats()))
            throw new AppException(ErrorCode.INVALID_SEATS);

        if(isValidLicensePlate(carRequest.getLicensePlate()))
            throw new AppException(ErrorCode.INVALID_VIETNAM_LICENSE);

        CarType cartype = modelMapper.map(carTypeService.findById(carRequest.getCarType().getCarTypeId()), CarType.class);
        Car car = modelMapper.map(carRequest, Car.class);

        cartype.addCar(car);

        Car newCar = carRepository.save(car);
        CarDTO carResponse =  modelMapper.map(newCar, CarDTO.class);
        carResponse.setCarType(modelMapper.map(cartype, CarTypeDTO.class));
        return carResponse;
    }

    @Override
    public CarDTO update(final CarDTO carRequest, final Integer id) {
        if(!carRepository.existsById(id))
            throw new AppException(ErrorCode.NOT_FOUND_ID);

        if(carRepository.existsBylicensePlateAndCarIdNot(carRequest.getLicensePlate(), id))
            throw new AppException(ErrorCode.EXIST);

        if(!isValidNumberOfSeat(carRequest.getNumberOfSeats()))
            throw new AppException(ErrorCode.INVALID_SEATS);

        if(isValidLicensePlate(carRequest.getLicensePlate()))
            throw new AppException(ErrorCode.INVALID_VIETNAM_LICENSE);

        Car car = modelMapper.map(this.findById(id), Car.class);

        CarType newCarType = modelMapper.map(carTypeService.findById(carRequest.getCarType().getCarTypeId()), CarType.class);

        CarType oldCartype = car.getCarType();

        if(newCarType.getCarTypeId() != oldCartype.getCarTypeId()) {
            newCarType.addCar(car);
        }

        car.setLicensePlate(carRequest.getLicensePlate().trim());
        car.setImage(carRequest.getImage().trim());
        car.setNumberOfSeats(carRequest.getNumberOfSeats());
        car.setStatus(carRequest.getStatus().trim());
        Car updateCar = carRepository.save(car);
        CarDTO carResponse = modelMapper.map(updateCar, CarDTO.class);
        carResponse.setCarType(modelMapper.map(newCarType, CarTypeDTO.class));
        return carResponse;
    }

    @Override
    public void deleteById(final Integer id) {
        if(!carRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }
        carRepository.deleteById(id);
    }

    @Override
    public CarDTO findById(final Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
        CarType cartype = car.getCarType();
        CarDTO carResponse = modelMapper.map(car, CarDTO.class);
        carResponse.setCarType(modelMapper.map(cartype, CarTypeDTO.class));
        return carResponse;
    }

    @Override
    public List<CarDTO> findAll() {
        return this.convertToCarDTO(carRepository.findAll());
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

        Page<Car> cars = carRepository.findAll(pageable);

        List<CarDTO> carsResponse = convertToCarDTO(cars);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(cars.getTotalPages())
                .totalElements(cars.getTotalElements())
                .items(carsResponse)
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

        Page<Car> cars = carRepository.findAll(pageable);

        List<CarDTO> carsResponse = convertToCarDTO(cars);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(cars.getTotalPages())
                .totalElements(cars.getTotalElements())
                .items(carsResponse)
                .build();
    }

    public PageResponse<?> findAllPagination(final Integer pageSize, final Integer pageNo) {
        return this.findAllPaginationWithSortBy(pageSize, pageNo, null);
    }

    public List<CarDTO> convertToCarDTO(final List<Car> cars) {
        List<CarDTO> carsDTO = cars.stream()
                .map(car -> {
                    CarDTO carResponse = modelMapper.map(car, CarDTO.class);

                    CarTypeDTO carTypeResponse = modelMapper.map(car.getCarType(), CarTypeDTO.class);
                    carResponse.setCarType(carTypeResponse);

                    return carResponse;
                }).collect(Collectors.toList());
        return carsDTO;
    }

    public List<CarDTO> convertToCarDTO(final Page<Car> cars) {
        List<CarDTO> carsDTO = cars.stream()
                .map(car -> {
                    CarDTO carResponse = modelMapper.map(car, CarDTO.class);

                    CarTypeDTO carTypeResponse = modelMapper.map(car.getCarType(), CarTypeDTO.class);
                    carResponse.setCarType(carTypeResponse);

                    return carResponse;
                }).collect(Collectors.toList());
        return carsDTO;
    }

//    public CarPageResponse findAllPaginationWithSearch(Integer pageSize, Integer pageNo,
//                                                       String licensePlate, Integer numberOfSeats, String carType, String status) {
//        // Tạo đối tượng phân trang
//        CarPageResponse carPageResponse = new CarPageResponse(pageSize, pageNo * pageSize);
//
//        // Tìm kiếm theo tên và phân trang
//        Page<Car> carPageResult = carRepository.findByLicensePlateContainingIgnoreCaseAndCarType_NameContainingIgnoreCaseAndNumberOfSeatsAndStatusContainingIgnoreCase(
//                licensePlate, carType, numberOfSeats, status, carPageResponse);
//
//        System.out.println(carPageResult.getContent().size());
//        // Chuyển đổi kết quả thành DTO
//        List<CarDTO> cars = carPageResult.hasContent()
//                ? this.convertToCarDTO(carPageResult.getContent())
//                : new ArrayList<>();
//
//        // Tạo đối tượng phản hồi và thiết lập dữ liệu
//        carPageResponse.setCars(cars);
//        carPageResponse.setTotalElements(findAllCar(licensePlate, numberOfSeats, carType, status).size());
//
//        return carPageResponse;
//    }
//
//    public List<CarDTO> findAllCar(String licensePlate, Integer numberOfSeats, String carType, String status) {
//        // Tim kiem theo ten loai xe
//        List<Car> carSearch = carRepository.findByLicensePlateContainingIgnoreCaseAndCarType_NameContainingIgnoreCaseAndNumberOfSeatsAndStatusContainingIgnoreCase(
//                licensePlate, carType, numberOfSeats, status);
//        return convertToCarDTO(carSearch);
//    }

    public Page<Car> searchCars(String licensePlate, String carTypeName, Integer numberOfSeats, String status, Pageable pageable) {
        return carRepository.findAll(CarSpecification.searchByCriteria(licensePlate, carTypeName, numberOfSeats, status), pageable);
    }

    public CarPageResponse findAllPaginationWithSearch(Integer pageSize, Integer pageNo, String licensePlate, String carTypeName, Integer numberOfSeats, String status) {
        // Tạo đối tượng phân trang
        CarPageResponse carPageResponse = new CarPageResponse(pageSize, pageNo * pageSize);

        // Tìm kiếm theo tên và phân trang
        Page<Car> carPageResult = carRepository.searchCars(
                licensePlate, numberOfSeats, carTypeName, status, carPageResponse);

        // Chuyển đổi kết quả thành DTO
        List<CarDTO> cars = carPageResult.hasContent()
                ? this.convertToCarDTO(carPageResult.getContent())
                : new ArrayList<>();

        // Tạo đối tượng phản hồi và thiết lập dữ liệu
        carPageResponse.setCars(cars);
        carPageResponse.setTotalElements(findAllCar(licensePlate, carTypeName, numberOfSeats, status).size());

        return carPageResponse;
    }


    public List<CarDTO> findAllCar(String licensePlate, String carTypeName, Integer numberOfSeats, String status) {
        // Tim kiem theo ten loai xe
        List<Car> carsSearch = carRepository.searchCars(
                licensePlate, numberOfSeats, carTypeName, status);
        return convertToCarDTO(carsSearch);
    }
}