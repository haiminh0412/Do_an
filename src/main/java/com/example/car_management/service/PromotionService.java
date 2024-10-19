package com.example.car_management.service;

import com.example.car_management.dto.CarTypeDTO;
import com.example.car_management.dto.PromotionDTO;
import com.example.car_management.entity.CarType;
import com.example.car_management.entity.Promotion;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.pagination.response.CarTypePageResponse;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.repository.IPromotionRepository;
import com.example.car_management.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PromotionService {

    @Autowired
    private final IPromotionRepository promotionRepository;

    @Autowired
    private final ModelMapper modelMapper;

    // Tính tổng giá sau khi áp dụng khuyến mãi
    public Long calculateTotalPriceWithPromotion(final String code, final Long originalPrice) {
        Optional<Promotion> promotionOpt = promotionRepository.findByCode(code);

        // Nếu không tìm thấy khuyến mãi hoặc không hợp lệ, trả về giá gốc
        if (promotionOpt.isEmpty()) {
            return originalPrice;
        }

        Promotion promotion = promotionOpt.get();

        // Kiểm tra thời gian áp dụng khuyến mãi
        LocalDate today = LocalDate.now();
        if (today.isBefore(promotion.getStartDate()) || today.isAfter(promotion.getEndDate())) {
            return originalPrice; // Không nằm trong khoảng thời gian khuyến mãi
        }

        // Kiểm tra giới hạn số lần sử dụng
        if (promotion.getUsageLimit() > 0) {
            promotion.setUsageLimit(promotion.getUsageLimit() - 1);
            promotionRepository.save(promotion);
        } else if (promotion.getUsageLimit() == 0) {
            return originalPrice; // Mã khuyến mãi đã hết lượt sử dụng
        }

        // Tính tổng giá sau khi đã giảm giá
        Long discountAmount = Math.round(originalPrice * (promotion.getDiscountPercentage() / 100.0));
        Long totalPrice = originalPrice - discountAmount;

        // Đảm bảo tổng giá không nhỏ hơn 0
        return totalPrice < 0 ? 0L : totalPrice;
    }

    // Thêm khuyến mãi mới
    public PromotionDTO addPromotion(PromotionDTO promotionDTO) {
        if(promotionDTO.getCode().length() < 5 || promotionDTO.getCode().length() > 10)
            throw new AppException(ErrorCode.INVALID_PROMOION);

        if(promotionDTO.getStartDate().isAfter(promotionDTO.getEndDate()))
            throw new AppException(ErrorCode.INVALID_PROMOION_DATE);

        // Chuyển đổi PromotionDTO thành Promotion
        Promotion promotion = modelMapper.map(promotionDTO, Promotion.class);


        // Kiểm tra xem mã khuyến mãi đã tồn tại chưa
        if (promotionRepository.findByCode(promotion.getCode()).isPresent()) {
            throw new AppException(ErrorCode.EXIST);
        }

        // Lưu và chuyển đổi Promotion thành PromotionDTO để trả về
        Promotion savedPromotion = promotionRepository.save(promotion);
        return modelMapper.map(savedPromotion, PromotionDTO.class);
    }

    public PromotionDTO findByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code).orElseThrow(() -> new AppException(ErrorCode.NOT_EXIST));
        return modelMapper.map(promotion, PromotionDTO.class);
    }

    // Cập nhật khuyến mãi
    public PromotionDTO updatePromotion(Long id, PromotionDTO updatedPromotionDTO) {
        // Tìm khuyến mãi theo mã
        if(updatedPromotionDTO.getCode().length() < 5 || updatedPromotionDTO.getCode().length() > 10)
            throw new AppException(ErrorCode.INVALID_PROMOION);

        if(updatedPromotionDTO.getStartDate().isAfter(updatedPromotionDTO.getEndDate()))
            throw new AppException(ErrorCode.INVALID_PROMOION_DATE);

        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));

        // Chuyển đổi PromotionDTO thành Promotion và cập nhật thông tin
        Promotion updatedPromotion = modelMapper.map(updatedPromotionDTO, Promotion.class);
        existingPromotion.setDescription(updatedPromotion.getDescription());
        existingPromotion.setDiscountPercentage(updatedPromotion.getDiscountPercentage());
        existingPromotion.setStartDate(updatedPromotion.getStartDate());
        existingPromotion.setEndDate(updatedPromotion.getEndDate());
        existingPromotion.setUsageLimit(updatedPromotion.getUsageLimit());

        // Lưu và chuyển đổi Promotion thành PromotionDTO để trả về
        Promotion savedPromotion = promotionRepository.save(existingPromotion);
        return modelMapper.map(savedPromotion, PromotionDTO.class);
    }

    public PromotionDTO updatePromotion(String code, PromotionDTO updatedPromotionDTO) {
        // Tìm khuyến mãi theo mã
        Promotion existingPromotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));

        // Chuyển đổi PromotionDTO thành Promotion và cập nhật thông tin
        Promotion updatedPromotion = modelMapper.map(updatedPromotionDTO, Promotion.class);
        existingPromotion.setDescription(updatedPromotion.getDescription());
        existingPromotion.setDiscountPercentage(updatedPromotion.getDiscountPercentage());
        existingPromotion.setStartDate(updatedPromotion.getStartDate());
        existingPromotion.setEndDate(updatedPromotion.getEndDate());
        existingPromotion.setUsageLimit(updatedPromotion.getUsageLimit());

        // Lưu và chuyển đổi Promotion thành PromotionDTO để trả về
        Promotion savedPromotion = promotionRepository.save(existingPromotion);
        return modelMapper.map(savedPromotion, PromotionDTO.class);
    }

    // Xóa khuyến mãi
    public void deletePromotion(Long id) {
        // Tìm khuyến mãi theo mã
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));

        // Xóa khuyến mãi
        promotionRepository.delete(existingPromotion);
    }

    // Lấy tất cả khuyến mãi
    public List<PromotionDTO> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream()
                .map(promotion -> modelMapper.map(promotion, PromotionDTO.class))
                .collect(Collectors.toList());
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

        Page<Promotion> promotions = promotionRepository.findAll(pageable);

        List<PromotionDTO> promotionsResponse = promotions.stream().map(
                        promotion -> modelMapper.map(promotions, PromotionDTO.class))
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(promotions.getTotalPages())
                .totalElements(promotions.getTotalElements())
                .items(promotionsResponse)
                .build();
    }

    public List<PromotionDTO> findAllPaginationWithSearch(String code, LocalDate startDate, LocalDate endDate) {
        List<Promotion> promotions =  promotionRepository.findPromotionsByAdvancedSearch(code, startDate, endDate);
        return promotions.stream().map(
                        promotion -> modelMapper.map(promotion, PromotionDTO.class))
                .toList();
    }
}