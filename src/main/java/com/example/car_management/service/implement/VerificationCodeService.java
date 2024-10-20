package com.example.car_management.service.implement;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.HistoryBookingDTO;
import com.example.car_management.dto.VerificationCodeResponse;
import com.example.car_management.entity.Booking;
import com.example.car_management.entity.VerificationCode;
import com.example.car_management.Enum.BookingStatus;
import com.example.car_management.Enum.VerificationCodeType;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.IBookingRepository;
import com.example.car_management.repository.IVerificationCodeRepository;
import com.example.car_management.service.CodeGeneratorService;
import com.example.car_management.service.EmailService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VerificationCodeService {
    private static final int EXPIRED = 5;

    @Autowired
    private final IVerificationCodeRepository verificationCodeRepository;

    @Autowired
    private final IBookingRepository bookingRepository;

    @Autowired
    private final CodeGeneratorService codeGeneratorService;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final BookingSeatService bookingSeatService;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final HistoryBookingsService historyBookingsService;

    @Autowired
    private final ModelMapper modelMapper;

//    public VerificationCodeResponse confirmCancellation(final Integer bookingId) {
//        // Tìm BookingDTO từ bookingId
//        BookingDTO bookingDTO = bookingService.findById(bookingId);
//        Booking booking = modelMapper.map(bookingDTO, Booking.class);
//
//        // Kiểm tra xem mã xác nhận đã tồn tại chưa
//        Optional<VerificationCode> existingVerificationCode = verificationCodeRepository.findByBookingAndType(
//                booking, VerificationCodeType.CANCEL_TICKET.getDescription());
//
//        if (existingVerificationCode.isPresent()) {
//            // Nếu mã đã tồn tại, không tạo mã mới và không gửi email lại
//            VerificationCodeResponse verificationCodeResponse = modelMapper.map(existingVerificationCode.get(), VerificationCodeResponse.class);
//            verificationCodeResponse.setBooking(bookingDTO);
//            return verificationCodeResponse;
//        }
//
//        // Nếu chưa có mã xác nhận, tạo mã mới
//        String cancelCode = codeGeneratorService.generateRandomCode();
//
//        VerificationCode verificationCode = VerificationCode.builder()
//                .code(cancelCode)
//                .type(VerificationCodeType.CANCEL_TICKET.getDescription())
//                .booking(booking)
//                .build();
//
//        // Lưu mã xác nhận vào CSDL
//        booking.addVerificationCode(verificationCode);
//        VerificationCode newVerificationCode = verificationCodeRepository.save(verificationCode);
//
//        // Tạo phản hồi cho mã xác nhận
//        VerificationCodeResponse verificationCodeResponse = modelMapper.map(newVerificationCode, VerificationCodeResponse.class);
//        verificationCodeResponse.setBooking(bookingDTO);
//
//        // Gửi email xác nhận hủy vé
//        emailService.sendCancellationConfirmationEmail(cancelCode, bookingDTO);
//
//        return verificationCodeResponse;
//    }

    public VerificationCodeResponse confirmCancellation(final Integer bookingId) {
        // Tìm BookingDTO từ bookingId
        BookingDTO bookingDTO = bookingService.findById(bookingId);
        Booking booking = modelMapper.map(bookingDTO, Booking.class);

        // Tạo mã xác nhận hủy vé mới
        String cancelCode = codeGeneratorService.generateRandomCode();

        VerificationCode verificationCode = VerificationCode.builder()
                .code(cancelCode)
                .type(VerificationCodeType.CANCEL_TICKET.getDescription())
                .booking(booking)
                .build();

        // Lưu mã xác nhận vào CSDL
        booking.addVerificationCode(verificationCode);
        VerificationCode newVerificationCode = verificationCodeRepository.save(verificationCode);

        // Tạo phản hồi cho mã xác nhận
        VerificationCodeResponse verificationCodeResponse = modelMapper.map(newVerificationCode, VerificationCodeResponse.class);
        verificationCodeResponse.setBooking(bookingDTO);

        // Gửi email xác nhận hủy vé
        emailService.sendCancellationConfirmationEmail(cancelCode, bookingDTO);

        return verificationCodeResponse;
    }

    public BookingDTO cancelBooking(final Integer bookingId, final String cancelCode) {
        BookingDTO bookingDTO = bookingService.findById(bookingId);
        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        VerificationCode verificationCode = verificationCodeRepository.findByCodeAndBooking(cancelCode, booking);

        if(!isValidVerificationCode(verificationCode))
            throw new AppException(ErrorCode.INVALID_VERIFICATION_CODE);

        bookingDTO.setStatus(BookingStatus.CANCELLED.getStatus());
        bookingSeatService.cancelBookingSeat(booking);
        HistoryBookingDTO historyBookingDTO = modelMapper.map(historyBookingsService.getHistoryBookingById(bookingId), HistoryBookingDTO.class);
        BookingDTO cancelBooking = bookingService.cancelBooking(bookingDTO, historyBookingDTO);
        return cancelBooking;
    }

    private boolean isValidVerificationCode(VerificationCode verificationCode) {
        LocalDateTime sendAt = verificationCode.getSentAt();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(sendAt, now);

        // Khoảng thời gian 5 phút
        Duration fiveMinutes = Duration.ofMinutes(EXPIRED);

        // Kiểm tra xem khoảng thời gian có nhỏ hơn hoặc bằng 5 phút không
        return duration.compareTo(fiveMinutes) < 0;
    }
}
