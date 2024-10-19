package com.example.car_management.service;

import com.example.car_management.dto.CustomerDTO;
import com.example.car_management.dto.response.HistoryBookingResponse;
import com.example.car_management.entity.HistoryBooking;
import com.example.car_management.repository.IBookingRepository;
import com.example.car_management.service.implement.BookingService;
import com.example.car_management.service.implement.HistoryBookingsService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HistoryBookingService {
    @Autowired
    private final IBookingRepository bookingRepository;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final HistoryBookingsService historyBookingsService;

    public List<HistoryBookingResponse> getBookedTickets(final CustomerDTO customerRequest) {
        List<HistoryBookingResponse> historyBookingResponses = new ArrayList<>();

        List<Object[]> allbookedTicket = bookingRepository.getAllBookedTickets(customerRequest.getName(),
                customerRequest.getEmail(), customerRequest.getPhone());

        for (Object[] obj : allbookedTicket) {
            Integer bookingId = (Integer) obj[0];
            HistoryBooking historyBookings = historyBookingsService.getHistoryBookingById(bookingId);

            if(historyBookings != null) {
                System.out.println("hi");
            }

            String startDestination = (String) obj[1];
            String endDestination = (String) obj[2];
            LocalDate departureDate = ((Date) obj[3]).toLocalDate();
            LocalDateTime bookingAt = ((Timestamp) obj[4]).toLocalDateTime();
            String status = (String) obj[5];
            String departure = (String) obj[6];
            String destination = (String) obj[7];
            Long price = (Long) obj[8];
            LocalTime departureTime = ((Time) obj[9]).toLocalTime();
            LocalTime destinationTime = ((Time) obj[10]).toLocalTime();
            String carType = (String) obj[11];
            String licensePlate = (String) obj[12];
            String[] bookedSeats = (obj[13] != null) ? ((String) obj[13]).split(",") : new String[0];
            int numberSeats = (historyBookings != null ? historyBookings.getSeatCount() : bookedSeats.length);
            Long totalPrice = 0l;

            if(historyBookings != null) {
                totalPrice = (long) historyBookings.getTotalPrice();
                System.out.println("id: " + bookingId + " totalPrice: " + totalPrice);
            }
            else {
                totalPrice = bookedSeats.length * price;
            }

            HistoryBookingResponse historyBooking = HistoryBookingResponse.builder()
                    .bookingId(bookingId)
                    .startDestination(startDestination)
                    .endDestination(endDestination)
                    .departureDate(departureDate)
                    .bookingAt(bookingAt)
                    .status(status)
                    .departure(departure)
                    .destination(destination)
                    .price(price)
                    .departureTime(departureTime)
                    .destinationTime(destinationTime)
                    .carType(carType)
                    .licensePlate(licensePlate)
                    .bookedSeats(bookedSeats)
                    .totalPrice(totalPrice)
                    .numberSeats(numberSeats)
                    .build();

            historyBookingResponses.add(historyBooking);
        }
        return historyBookingResponses;
    }

    public List<HistoryBookingResponse> getBookedTickets(final CustomerDTO customerRequest, int ticketId) {
        List<HistoryBookingResponse> historyBookingResponses = new ArrayList<>();

        List<Object[]> allbookedTicket = bookingRepository.searchBooking(ticketId, customerRequest.getName(),
                customerRequest.getEmail(), customerRequest.getPhone());

        for (Object[] obj : allbookedTicket) {
            Integer bookingId = (Integer) obj[0];
            HistoryBooking historyBookings = historyBookingsService.getHistoryBookingById(bookingId);

            if(historyBookings != null) {
                System.out.println("hi");
            }

            String startDestination = (String) obj[1];
            String endDestination = (String) obj[2];
            LocalDate departureDate = ((Date) obj[3]).toLocalDate();
            LocalDateTime bookingAt = ((Timestamp) obj[4]).toLocalDateTime();
            String status = (String) obj[5];
            String departure = (String) obj[6];
            String destination = (String) obj[7];
            Long price = (Long) obj[8];
            LocalTime departureTime = ((Time) obj[9]).toLocalTime();
            LocalTime destinationTime = ((Time) obj[10]).toLocalTime();
            String carType = (String) obj[11];
            String licensePlate = (String) obj[12];
            String[] bookedSeats = (obj[13] != null) ? ((String) obj[13]).split(",") : new String[0];
            int numberSeats = (historyBookings != null ? historyBookings.getSeatCount() : bookedSeats.length);
            Long totalPrice = 0l;

            if(historyBookings != null) {
                totalPrice = (long) historyBookings.getTotalPrice();
                System.out.println("id: " + bookingId + " totalPrice: " + totalPrice);
            }
            else {
                totalPrice = bookedSeats.length * price;
            }

            HistoryBookingResponse historyBooking = HistoryBookingResponse.builder()
                    .bookingId(bookingId)
                    .startDestination(startDestination)
                    .endDestination(endDestination)
                    .departureDate(departureDate)
                    .bookingAt(bookingAt)
                    .status(status)
                    .departure(departure)
                    .destination(destination)
                    .price(price)
                    .departureTime(departureTime)
                    .destinationTime(destinationTime)
                    .carType(carType)
                    .licensePlate(licensePlate)
                    .bookedSeats(bookedSeats)
                    .totalPrice(totalPrice)
                    .numberSeats(numberSeats)
                    .build();

            historyBookingResponses.add(historyBooking);
        }
        return historyBookingResponses;
    }

    public HistoryBookingResponse getBookedTicketByBookingId(final Integer bookingId) {
        List<Object[]> objects = bookingRepository.getBookedTicketByBookingId(bookingId);
        Object[] obj = objects.get(0);

        HistoryBooking historyBookings = historyBookingsService.getHistoryBookingById(bookingId);

        if(historyBookings != null) {
            System.out.println("hi");
        }

        String startDestination = (String) obj[1];
        String endDestination = (String) obj[2];
        LocalDate departureDate = ((Date) obj[3]).toLocalDate();
        LocalDateTime bookingAt = ((Timestamp) obj[4]).toLocalDateTime();
        String status = (String) obj[5];
        String departure = (String) obj[6];
        String destination = (String) obj[7];
        Long price = (Long) obj[8];
        LocalTime departureTime = ((Time) obj[9]).toLocalTime();
        LocalTime destinationTime = ((Time) obj[10]).toLocalTime();
        String carType = (String) obj[11];
        String licensePlate = (String) obj[12];
        String[] bookedSeats = (obj[13] != null) ? ((String) obj[13]).split(",") : new String[0];

        int numberSeats = (historyBookings != null ? historyBookings.getSeatCount() : bookedSeats.length);
        Long totalPrice = 0l;

        if(historyBookings != null) {
            totalPrice = (long) historyBookings.getTotalPrice();
            System.out.println("id: " + bookingId + " totalPrice: " + totalPrice);
        }
        else {
            totalPrice = bookedSeats.length * price;
        }

        HistoryBookingResponse historyBooking = HistoryBookingResponse.builder()
                .bookingId(bookingId)
                .startDestination(startDestination)
                .endDestination(endDestination)
                .departureDate(departureDate)
                .bookingAt(bookingAt)
                .status(status)
                .departure(departure)
                .destination(destination)
                .price(price)
                .departureTime(departureTime)
                .destinationTime(destinationTime)
                .carType(carType)
                .licensePlate(licensePlate)
                .bookedSeats(bookedSeats)
                .totalPrice(totalPrice)
                .numberSeats(numberSeats)
                .build();

        return historyBooking;
    }
}
