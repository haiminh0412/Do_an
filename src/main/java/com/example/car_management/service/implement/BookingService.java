package com.example.car_management.service.implement;

import com.example.car_management.dto.*;
import com.example.car_management.entity.Booking;
import com.example.car_management.entity.Customer;
import com.example.car_management.entity.TripDetail;
import com.example.car_management.Enum.BookingStatus;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.IBookingRepository;
import com.example.car_management.repository.ITripDetailRepository;
import com.example.car_management.service.Interface.ITripService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class BookingService {
    @Autowired
    private final IBookingRepository bookingRepository;

    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final TripDetailService tripDetailService;

    @Autowired
    private final ITripDetailRepository tripDetailRepository;

    @Autowired
    private final HistoryBookingsService historyBookingsService;

    @Autowired
    private final ModelMapper modelMapper;
    
    public BookingDTO insert(final BookingDTO bookingRequest) {
        // them khach hang vao csdl
        CustomerDTO customerDTO = customerService.insert(bookingRequest.getCustomer());
        Customer customer = modelMapper.map(customerService.findById(customerDTO.getCustomerId()), Customer.class);

        TripDetail tripDetail = modelMapper.map(
                tripDetailService.findById(bookingRequest.getTripDetail().getTripDetailId()), TripDetail.class);

        // map DTO -> Entity
        Booking booking = new Booking().builder()
                .tripDetail(tripDetail)
                .customer(customer)
                .startDestination(bookingRequest.getStartDestination())
                .endDestination(bookingRequest.getEndDestination())
                .departureDate(bookingRequest.getDepartureDate())
                .status(String.valueOf(BookingStatus.CONFIRMED.getStatus()))
                .build();

        customer.addBooking(booking);
        tripDetail.addBooking(booking);

        Booking newBooking = bookingRepository.save(booking);

        // map Entity -> DTO
        BookingDTO bookingResponse = modelMapper.map(newBooking, BookingDTO.class);
        bookingResponse.setCustomer(modelMapper.map(customer, CustomerDTO.class));
        bookingResponse.setTripDetail(modelMapper.map(tripDetail, TripDetailDTO.class));
        return bookingResponse;
    }

    public BookingDTO cancelBooking(BookingDTO bookingRequest, HistoryBookingDTO historyBookingDTO) {
        Booking booking = modelMapper.map(bookingRequest, Booking.class);
        if(bookingRepository.existsById(bookingRequest.getBookingId())) {
            Booking cancelBooking = bookingRepository.save(booking);
            HistoryBookingDTO historyBookingDTOResponse = HistoryBookingDTO.builder()
                    .booking(bookingRequest)
                    .seatCount(historyBookingDTO.getSeatCount())
                    .totalPrice(historyBookingDTO.getTotalPrice())
                    .build();
            historyBookingsService.createHistoryBooking(historyBookingDTOResponse);
            return modelMapper.map(cancelBooking, BookingDTO.class);
        }
        return null;
    }

    public List<BookingDTO> findAll() {
        List<BookingDTO> bookings = new ArrayList<>();
        // Ánh xạ kết quả sang TripDetailDTO và thêm vào danh sách
        for (Booking booking : bookingRepository.findAll()) {
            BookingDTO bookingResponse = modelMapper.map(booking, BookingDTO.class);

            // Thêm vào danh sách
            bookings.add(bookingResponse);
        }
        return bookings;
    }

    public BookingDTO findById(final Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
        return modelMapper.map(booking, BookingDTO.class);
    }

//    public List<BookingDTO> getBookedTickets(CustomerDTO customerRequest) {
//        Customer customer = modelMapper.map(customerService.findByCustomer(customerRequest), Customer.class);
//        List<BookingDTO> bookedTickets = bookingRepository.findByCustomer(customer).stream().map(
//                booking -> {
//                    BookingDTO bookingResponse = modelMapper.map(booking, BookingDTO.class);
//
//                    CustomerDTO customerResponse = modelMapper.map(booking.getCustomer(), CustomerDTO.class);
//                    bookingResponse.setCustomer(customerResponse);
//
//                    TripDetailDTO tripDetailResponse = modelMapper.map(booking.getTripDetail(), TripDetailDTO.class);
//                    bookingResponse.setTripDetail(tripDetailResponse);
//
//                    return bookingResponse;
//
//                }).collect(Collectors.toList());
//        return bookedTickets;
//    }

    // Phương thức này sẽ chạy kiểm tra trạng thái vé liên tục mỗi 5 phút
    @Scheduled(fixedRate = 100000) // 100000 milliseconds = 100 giây
    public void updateBookingStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingsToUpdate = new ArrayList<>();

        // Lấy tất cả các vé có trạng thái không phải là "Cancelled"
        List<Booking> bookings = bookingRepository.findAllByStatusNot("Cancelled");

        for (Booking booking : bookings) {
            TripDetail tripDetail = tripDetailRepository.findById(booking.getTripDetail().getTripDetailId()).orElse(null);

            if (tripDetail != null) {
                // Thời gian bắt đầu và kết thúc của chuyến xe
                LocalDateTime tripStartTime = LocalDateTime.of(booking.getDepartureDate(), tripDetail.getDepartureTime());
                LocalDateTime tripEndTime = LocalDateTime.of(booking.getDepartureDate(), tripDetail.getDestinationTime());

                // Cập nhật trạng thái vé
                if (now.isAfter(tripEndTime) || now.isEqual(tripEndTime)) {
                    booking.setStatus("Completed");
                } else if (now.isBefore(tripStartTime)) {
                    booking.setStatus("Confirmed");
                }

                // Nếu trạng thái có thay đổi, thêm vào danh sách để lưu
                bookingsToUpdate.add(booking);
            }
        }

        // Lưu tất cả các vé đã cập nhật một lần
        if (!bookingsToUpdate.isEmpty()) {
            bookingRepository.saveAll(bookingsToUpdate);

            // Cập nhật lịch sử cho từng booking
            for (Booking booking : bookingsToUpdate) {
             //   updateStatus(booking);
            }
        }
    }

    private void updateStatus(Booking booking) {
        if(booking == null)
            return;

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        HistoryBookingDTO historyBookingDTO = modelMapper.map(historyBookingsService.getHistoryBookingById(bookingDTO.getBookingId()), HistoryBookingDTO.class);
        HistoryBookingDTO historyBookingDTOResponse = HistoryBookingDTO.builder()
                .booking(bookingDTO)
                .seatCount(historyBookingDTO.getSeatCount())
                .totalPrice(historyBookingDTO.getTotalPrice())
                .build();
        historyBookingsService.createHistoryBooking(historyBookingDTOResponse);
    }
}
