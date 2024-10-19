package com.example.car_management.service.implement;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.HistoryBookingDTO;
import com.example.car_management.dto.request.BookingRequest;
import com.example.car_management.dto.request.BookingSeatRequest;
import com.example.car_management.dto.response.BookingSeatResponse;
import com.example.car_management.dto.SeatDTO;
import com.example.car_management.entity.Booking;
import com.example.car_management.entity.BookingSeat;
import com.example.car_management.entity.Seat;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.IBookingSeatRepository;
import com.example.car_management.service.EmailService;
import com.example.car_management.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class BookingSeatService {
    @Autowired
    private final IBookingSeatRepository bookingSeatRepository;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final SeatService seatService;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final PromotionService promotionService;

    @Autowired
    private final HistoryBookingsService historyBookingsService;

    @Autowired
    private final ModelMapper modelMapper;

    public Set<Integer> findAllSeatIdByConditional(final Integer tripDetailId, final LocalDate deparetureDate) {
        Set<Integer> seatIds = bookingSeatRepository.findSeatsIdByDepartureDateAndTripDetail(deparetureDate, tripDetailId);
        return seatIds;
    }

    public List<BookingSeatResponse> findAll() {
        List<BookingSeatResponse> bookingSeats = new ArrayList<>();
        // Ánh xạ kết quả sang TripDetailDTO và thêm vào danh sách
        for (BookingSeat bookingSeat : bookingSeatRepository.findAll()) {
            BookingSeatResponse bookingSeatResponse = modelMapper.map(bookingSeat, BookingSeatResponse.class);
            BookingSeatRequest bookingSeatRequest = modelMapper.map(bookingSeat, BookingSeatRequest.class);
            // Thêm vào danh sách
            bookingSeats.add(bookingSeatResponse);
        }
        return bookingSeats;
    }

    public void sendEmail(final BookingSeatResponse bookingSeatResponse) {
        // Gửi email
        emailService.sendEmail(bookingSeatResponse);
    }

    public void sendSMS(final BookingSeatResponse bookingSeatResponse) {
        // Gửi SMS
        //smsService.sendSMS(bookingSeatResponse);
    }

    public BookingSeatResponse booking(final BookingRequest bookingRequest) {
        // Tạo đối tượng BookingDTO từ request
        BookingDTO bookingDTO = createBookingDTOFromRequest(bookingRequest);

        // Thêm booking vào cơ sở dữ liệu và map sang entity Booking
        Booking booking = saveBooking(bookingDTO);

        // Xử lý ghế và tạo liên kết BookingSeat cho từng ghế đã chọn
        List<SeatDTO> seats = processBookingSeats(bookingRequest.getSeatsIdSelected(), booking);

        // Tính tổng giá tiền
        Long totalPrice = calculateOriginalPrice(seats.size(), booking.getTripDetail().getPrice());

        HistoryBookingDTO historyBookingDTO = HistoryBookingDTO.builder()
                .booking(modelMapper.map(booking, BookingDTO.class))
                .totalPrice(totalPrice)
                .seatCount(seats.size())
                .build();

        historyBookingsService.createHistoryBooking(historyBookingDTO);

        // Tạo đối tượng phản hồi BookingSeatResponse
        return createBookingSeatResponse(modelMapper.map(booking, BookingDTO.class), seats, totalPrice);
    }

    public BookingSeatResponse bookingWithPromotionCode(final BookingRequest bookingRequest, final String promotionCode) {
        // Tạo đối tượng BookingDTO từ request
        BookingDTO bookingDTO = createBookingDTOFromRequest(bookingRequest);

        // Thêm booking vào cơ sở dữ liệu và map sang entity Booking
        Booking booking = saveBooking(bookingDTO);

        // Xử lý ghế và tạo liên kết BookingSeat cho từng ghế đã chọn
        List<SeatDTO> seats = processBookingSeats(bookingRequest.getSeatsIdSelected(), booking);

        // Tính tổng giá sau khi áp dụng khuyến mãi
        Long originalPrice = calculateOriginalPrice(seats.size(), booking.getTripDetail().getPrice());
        Long totalPrice = promotionService.calculateTotalPriceWithPromotion(promotionCode, originalPrice);

        HistoryBookingDTO historyBookingDTO = HistoryBookingDTO.builder()
                .booking(modelMapper.map(booking, BookingDTO.class))
                .totalPrice(totalPrice)
                .seatCount(seats.size())
                .build();

        historyBookingsService.createHistoryBooking(historyBookingDTO);

        // Tạo đối tượng phản hồi BookingSeatResponse
        return createBookingSeatResponse(modelMapper.map(booking, BookingDTO.class), seats, totalPrice);
    }

    private BookingDTO createBookingDTOFromRequest(final BookingRequest bookingRequest) {
        return BookingDTO.builder()
                .tripDetail(bookingRequest.getTripDetail())
                .customer(bookingRequest.getCustomer())
                .startDestination(bookingRequest.getStartDestination())
                .endDestination(bookingRequest.getEndDestination())
                .departureDate(bookingRequest.getDepartureDate())
                .build();
    }

    private Booking saveBooking(BookingDTO bookingDTO) {
        BookingDTO savedBookingDTO = bookingService.insert(bookingDTO);
        return modelMapper.map(savedBookingDTO, Booking.class);
    }

    private List<SeatDTO> processBookingSeats(final List<Integer> seatsIdSelected, final Booking booking) {
        List<SeatDTO> seatDTOList = new ArrayList<>();
        for (Integer seatId : seatsIdSelected) {
            SeatDTO seatDTO = seatService.findById(seatId);
            seatDTOList.add(seatDTO);

            Seat seat = modelMapper.map(seatDTO, Seat.class);

            // Tạo BookingSeat và liên kết với Booking và Seat
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setSeat(seat);
            bookingSeat.setBooking(booking);

            // Lưu bookingSeat vào cơ sở dữ liệu
            bookingSeatRepository.save(bookingSeat);
        }
        return seatDTOList;
    }

    private Long calculateOriginalPrice(final Integer numberOfSeats, final Long tripPrice) {
        return numberOfSeats * tripPrice;
    }

    private BookingSeatResponse createBookingSeatResponse(final BookingDTO bookingDTO,
                                                          final List<SeatDTO> seats,
                                                          final Long totalPrice) {
        return BookingSeatResponse.builder()
                .booking(bookingDTO)
                .seats(seats)
                .totalPrice(totalPrice)
                .build();
    }

    public void cancelBookingSeat(final Booking booking) {
        if (!bookingSeatRepository.existsByBooking(booking))
            throw new AppException(ErrorCode.NOT_EXIST);

        for(BookingSeat bookingSeat : bookingSeatRepository.findAll()) {
            if(Objects.equals(bookingSeat.getBooking().getBookingId(), booking.getBookingId())) {
                bookingSeatRepository.deleteById(bookingSeat.getBookingSeatId());
                return;
            }
        }
    }
}

