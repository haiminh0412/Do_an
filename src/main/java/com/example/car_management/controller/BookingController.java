package com.example.car_management.controller;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.CustomerDTO;
import com.example.car_management.dto.response.ApiResponse;
import com.example.car_management.dto.response.HistoryBookingResponse;
import com.example.car_management.service.HistoryBookingService;
import com.example.car_management.service.implement.BookingService;
import com.example.car_management.service.implement.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/booking")
@Tag(name = "Booking Controller", description = "Quản lý các thao tác đặt vé")
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final CustomerService customerService;

    @Autowired
    private final HistoryBookingService historyBookingService;

    @GetMapping
    @Operation(summary = "Retrieve all bookings", description = "Returns a list of all bookings in the system")
    public ResponseEntity<?> findAllBooking() {
        List<BookingDTO> bookings = bookingService.findAll();
        ApiResponse<List<BookingDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookings);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/history-booked")
    @Operation(summary = "Retrieve booked tickets by customer", description = "Returns a list of booked tickets filtered by customer name, phone, and email")
    public ResponseEntity<?> findAllBookedTickets(
            @RequestParam("name") final String name,
            @RequestParam("phone") final String phone,
            @RequestParam("email") final String email) {

        CustomerDTO customerRequest = CustomerDTO.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .build();

        List<HistoryBookingResponse> bookedTickets = historyBookingService.getBookedTickets(customerRequest);
        ApiResponse<List<HistoryBookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookedTickets);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/history-booked/all")
    @Operation(summary = "Retrieve booked tickets by customer", description = "Returns a list of booked tickets filtered by customer name, phone, and email")
    public ResponseEntity<?> findAllBookedTickets(
            @RequestParam("name") final String name,
            @RequestParam("phone") final String phone,
            @RequestParam("email") final String email,
            @RequestParam("bookingId") final Integer ticketId) {

        CustomerDTO customerRequest = CustomerDTO.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .build();

        List<HistoryBookingResponse> bookedTickets = historyBookingService.getBookedTickets(customerRequest, ticketId);
        ApiResponse<List<HistoryBookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookedTickets);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/history-booked/{bookingId}")
    @Operation(summary = "Retrieve a booked ticket by booking ID", description = "Returns the booked ticket details based on the booking ID")
    public ResponseEntity<?> findBookedTicketById(@PathVariable("bookingId") final Integer bookingId) {
        HistoryBookingResponse bookedTicket = historyBookingService.getBookedTicketByBookingId(bookingId);
        ApiResponse<HistoryBookingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookedTicket);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }



    @PostMapping
    @Operation(summary = "Create a new booking", description = "Adds a new booking with customer and trip details")
    public ResponseEntity<?> addBooking(@RequestBody BookingDTO bookingRequest) {
        BookingDTO bookingResponse = bookingService.insert(bookingRequest);
        ApiResponse<BookingDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(bookingResponse);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
