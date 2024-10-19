package com.example.car_management.service.implement;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.HistoryBookingDTO;
import com.example.car_management.entity.Booking;
import com.example.car_management.entity.HistoryBooking;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.IBookingRepository;
import com.example.car_management.repository.IHistoryBookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class HistoryBookingsService {
    @Autowired
    private IHistoryBookingRepository historyBookingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IBookingRepository bookingRepository;

    // Lấy tất cả các bản ghi HistoryBooking
    public List<HistoryBookingDTO> getAllHistoryBookings() {
        return historyBookingRepository.findAll().stream()
                .map(historyBooking -> {
                    HistoryBookingDTO historyBookingResponse = modelMapper.map(historyBooking, HistoryBookingDTO.class);

                    BookingDTO bookingDTO = modelMapper.map(historyBooking.getBooking(), BookingDTO.class);
                    historyBookingResponse.setBooking(bookingDTO);

                    return historyBookingResponse;
                }).collect(Collectors.toList());
    }

    // Lấy bản ghi HistoryBooking theo ID
    public HistoryBooking getHistoryBookingById(Integer bookingId) {
        for(HistoryBooking historyBooking : historyBookingRepository.findAll()) {
            if(Objects.equals(historyBooking.getBooking().getBookingId(), bookingId)) {
                return historyBooking;
            }
        }
        return null;
    }

    // Tạo mới một bản ghi HistoryBooking
    public HistoryBookingDTO createHistoryBooking(HistoryBookingDTO historyBookingDTO) {
        HistoryBooking historyBooking = modelMapper.map(historyBookingDTO, HistoryBooking.class);
        Booking booking = bookingRepository.findById(historyBookingDTO.getBooking().getBookingId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
        historyBooking.setBooking(booking);
        return modelMapper.map(historyBookingRepository.save(historyBooking), HistoryBookingDTO.class);
    }
}
