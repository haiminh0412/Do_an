package com.example.car_management.service.implement;

import com.example.car_management.dto.CarTypeDTO;
import com.example.car_management.pagination.response.CarTypePageResponse;
import com.example.car_management.entity.CarType;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.repository.ICarTypeRepository;
import com.example.car_management.service.Interface.ICarTypeService;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CarTypeService implements ICarTypeService {
    @Autowired
    private final ICarTypeRepository carTypeRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public CarTypeDTO insert(final CarTypeDTO carTypeRequest) {
        if(carTypeRepository.existsByName(carTypeRequest.getName()))
            throw new AppException(ErrorCode.EXIST);


        // convert DTO to entity
        CarType carType = modelMapper.map(carTypeRequest, CarType.class);

        CarType newCarType =  carTypeRepository.save(carType);

        // convert entity to DTO
        CarTypeDTO carTypeResponse = modelMapper.map(newCarType, CarTypeDTO.class);

        return carTypeResponse;
    }

    @Override
    public CarTypeDTO update(final CarTypeDTO carTypeRequest, final Integer id) {
        if(!carTypeRepository.existsById(id))
            throw new AppException(ErrorCode.NOT_FOUND_ID);


        if(carTypeRepository.existsByNameAndCarTypeIdNot(carTypeRequest.getName(), id))
            throw new AppException(ErrorCode.EXIST);

        CarType carType = modelMapper.map(this.findById(id), CarType.class);
        carType.setName(carTypeRequest.getName());

        CarType updateCarType = carTypeRepository.save(carType);

        CarTypeDTO carTypeResponse = modelMapper.map(updateCarType, CarTypeDTO.class);
        return carTypeResponse;
    }

    @Override
    public void deleteById(final Integer id) {
        if(!carTypeRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_ID);
        }
        carTypeRepository.deleteById(id);
    }

    @Override
    public CarTypeDTO findById(final Integer id) {
        CarType carType = carTypeRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND_ID));

        CarTypeDTO carTypeResponse = modelMapper.map(carType, CarTypeDTO.class);
        return carTypeResponse;
    }

    @Override
    public List<CarTypeDTO> findAll() {
        return this.convertToCarTypeDTO(carTypeRepository.findAll());
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

        Page<CarType> carTypes = carTypeRepository.findAll(pageable);

        List<CarTypeDTO> carTypesResponse = convertToCarTypeDTO(carTypes);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(carTypes.getTotalPages())
                .totalElements(carTypes.getTotalElements())
                .items(carTypesResponse)
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

        Page<CarType> carTypes = carTypeRepository.findAll(pageable);

        List<CarTypeDTO> carTypesResponse = convertToCarTypeDTO(carTypes);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(carTypes.getTotalPages())
                .totalElements(carTypes.getTotalElements())
                .items(carTypesResponse)
                .build();
    }

    public PageResponse<?> findAllPagination(final Integer pageSize, final Integer pageNo) {
        return this.findAllPaginationWithSortBy(pageSize, pageNo, null);
    }

    public CarTypePageResponse findAllPaginationWithSearch(Integer pageSize, Integer pageNo, String name) {
        // Tạo đối tượng phân trang
        CarTypePageResponse carTypePageResponse = new CarTypePageResponse(pageSize, pageNo * pageSize);

        // Tìm kiếm theo tên và phân trang
        Page<CarType> carTypePageResult = carTypeRepository.findByNameContainingIgnoreCase(name, carTypePageResponse);

        // Chuyển đổi kết quả thành DTO
        List<CarTypeDTO> carTypes = carTypePageResult.hasContent()
                ? this.convertToCarTypeDTO(carTypePageResult.getContent())
                : new ArrayList<>();

        // Tạo đối tượng phản hồi và thiết lập dữ liệu
        carTypePageResponse.setCarTypes(carTypes);
        carTypePageResponse.setTotalElements(findAllCarTypeByNameLike(name).size());

        return carTypePageResponse;
    }

    public List<CarTypeDTO> convertToCarTypeDTO(final List<CarType> carTypes) {
        List<CarTypeDTO> carTypesDTO = carTypes.stream().map(
                        cartype -> modelMapper.map(cartype, CarTypeDTO.class))
                .collect(Collectors.toList());
        return carTypesDTO;
    }

    public List<CarTypeDTO> convertToCarTypeDTO(final Page<CarType> carTypes) {
        List<CarTypeDTO> carTypesDTO = carTypes.stream().map(
                        cartype -> modelMapper.map(cartype, CarTypeDTO.class))
                .collect(Collectors.toList());
        return carTypesDTO;
    }

    public List<CarTypeDTO> findAllCarTypeByNameLike(final String name) {
        // Tim kiem theo ten loai xe
        List<CarType> carTypesSearch = carTypeRepository.findByNameContainingIgnoreCase(name);
        return convertToCarTypeDTO(carTypesSearch);
    }
}