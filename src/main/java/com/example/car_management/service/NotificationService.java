package com.example.car_management.service;

import com.example.car_management.Enum.BookingStatus;
import com.example.car_management.dto.BookingInfoDTO;
import com.example.car_management.dto.CustomerDTO;
import com.example.car_management.entity.*;
import com.example.car_management.Enum.NotificationType;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.IBookingRepository;
import com.example.car_management.repository.INotificationRepository;
import com.example.car_management.repository.IPromotionRepository;
import com.example.car_management.service.implement.CustomerService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class NotificationService {
    @Autowired
    private final IBookingRepository bookingRepository;

    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final INotificationRepository notificationRepository;

    @Autowired
    private final IPromotionRepository promotionRepository;

    @Autowired
    private final EmailService emailService;

    private Set<String> set = new HashSet<>();
  //  private LocalDateTime lastRunDate = null;



    @Scheduled(fixedRate = 60000) // Kiểm tra mỗi 1 phút
    @Transactional
    public void sendNotification() {
        List<BookingInfoDTO> bookingInfos = this.getUpcomingBookings();
        for (BookingInfoDTO bookingInfoDTO : bookingInfos) {
            Booking booking = bookingRepository.findById(bookingInfoDTO.getBookingId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
            if(!notificationRepository.existsByBooking(booking) ) {
                emailService.sendNotificationEmail(bookingInfoDTO);
                Notification notification = new Notification().builder()
                        .booking(booking)
                        .type(NotificationType.ONE_HOURS.getType())
                        .build();
                booking.addNotification(notification);
                notificationRepository.save(notification);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")  // Chạy mỗi giờ
    @Transactional
    public void sendPromotionNotification() {
        LocalDate today = LocalDate.now();

//        // Nếu lastRunDate chưa được khởi tạo hoặc đã qua ngày mới
//        if (!today.equals(lastRunDate)) {
//            set.clear();  // Reset set khi qua ngày mới
//            lastRunDate = today;
//        }

        List<Promotion> promotions = promotionRepository.findAll();
        for (Promotion promotion : promotions) {
            LocalDate startDate = promotion.getStartDate();
            if (startDate.equals(today)) {
                sendPromotionToCustomers(promotion);
            }
        }
    }

    private void sendPromotionToCustomers(Promotion promotion) {
        List<CustomerDTO> customers = customerService.findAll();
        Set<String> emails = new HashSet<>();

        for (CustomerDTO customer : customers) {
            emails.add(customer.getEmail());
        }

        for (String email : emails) {
            if (email.equals("haiminh042003@gmail.com")) {
                // Chỉ gửi email nếu email chưa nằm trong set
                if (!set.contains(email)) {
                    emailService.sendPromotionCodeToCustomer(email, promotion);
                    set.add(email);  // Thêm email vào set để không gửi lại trong cùng ngày
                }
            }
        }
    }

    public List<BookingInfoDTO> getUpcomingBookings() {
        List<Object[]> results = notificationRepository.findUpcomingBookings();
        List<BookingInfoDTO> bookingInfoDTOs = new ArrayList<>();

        for (Object[] result : results) {
            BookingInfoDTO dto = new BookingInfoDTO();
            dto.setCustomerId((Integer) result[0]);
            dto.setCustomerName((String) result[1]);
            dto.setPhone((String) result[2]);
            dto.setEmail((String) result[3]);
            dto.setBookingId((Integer) result[4]);
            dto.setStartDestination((String) result[5]);
            dto.setEndDestination((String) result[6]);
            dto.setDepartureDate(((Date) result[7]).toLocalDate());
            dto.setDepartureTime(((Time) result[8]).toLocalTime());
            dto.setDestinationTime(((Time) result[9]).toLocalTime());
            dto.setTripDeparture((String) result[10]);
            dto.setTripDestination((String) result[11]);

            bookingInfoDTOs.add(dto);
        }
        return bookingInfoDTOs;
    }
}