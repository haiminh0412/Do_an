package com.example.car_management.service;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.CarTypeDTO;
import com.example.car_management.dto.ReportBookingDTO;
import com.example.car_management.entity.Booking;
import com.example.car_management.entity.CarType;
import com.example.car_management.pagination.response.PageResponse;
import com.example.car_management.repository.IBookingRepository;
import com.example.car_management.service.implement.BookingService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ReportService {
    private final BookingService bookingService;
    private final IBookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    public PageResponse<?> searchBookings(
            Integer pageSize,
            Integer pageNo,
            String customerName,
            String trip,
            LocalDate startDate,
            LocalDate endDate,
            String status) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Object[]> reportBookings = bookingRepository.findBookingsWithDetails(
                customerName, trip, status, startDate, endDate, pageable);

        List<ReportBookingDTO> reportBookingsDTO = new ArrayList<>();
        for (Object[] booking : reportBookings.getContent()) {
            String customerNameResult = (String) booking[0];
            String tripResult = (String) booking[1];
            Integer ticketCount = (Integer) booking[2];

            LocalDateTime bookingDate = ((Timestamp) booking[3]).toLocalDateTime();
            LocalDate departureDate = ((Date) booking[4]).toLocalDate();

            Integer totalAmount = (Integer) booking[5];
            String statusResult = (String) booking[6];

            ReportBookingDTO reportBookingDTO = ReportBookingDTO.builder()
                    .customerName(customerNameResult)
                    .trip(tripResult)
                    .ticketCount(ticketCount)
                    .bookingDate(bookingDate)
                    .departureDate(departureDate)
                    .totalAmount(totalAmount)
                    .status(statusResult)
                    .build();

            reportBookingsDTO.add(reportBookingDTO);
        }

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(reportBookings.getTotalPages())
                .totalElements(reportBookings.getTotalElements())
                .items(reportBookingsDTO)
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

        Page<Object[]> reportBookings = bookingRepository.findBookingsWithDetails(pageable);

        List<ReportBookingDTO> reportBookingsDTO = new ArrayList<>();
        for (Object[] booking : reportBookings.getContent()) {
            String customerName = (String) booking[0];
            String trip = (String) booking[1];
            Integer ticketCount = (Integer) booking[2];

            // Chuyển đổi bookingDate từ java.sql.Timestamp sang LocalDateTime
            java.sql.Timestamp bookingTimestamp = (java.sql.Timestamp) booking[3];
            LocalDateTime bookingDate = bookingTimestamp.toLocalDateTime();

            // Chuyển đổi departureDate từ java.sql.Date sang LocalDate
            java.sql.Date departureDateSQL = (java.sql.Date) booking[4];
            LocalDate departureDate = departureDateSQL.toLocalDate();

            Integer totalAmount = (Integer) booking[5]; // Tổng tiền
            String status = (String) booking[6];

            ReportBookingDTO reportBookingDTO = ReportBookingDTO.builder()
                    .customerName(customerName)
                    .trip(trip)
                    .ticketCount(ticketCount)
                    .bookingDate(bookingDate)
                    .departureDate(departureDate) // sử dụng LocalDate cho departureDate
                    .totalAmount(totalAmount)
                    .status(status)
                    .build();

            // Thực hiện các xử lý cần thiết với dữ liệu
            reportBookingsDTO.add(reportBookingDTO);
        }

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(reportBookings.getTotalPages())
                .totalElements(reportBookings.getTotalElements())
                .items(reportBookingsDTO)
                .build();
    }

    public List<BookingDTO> convertToBookingDTO(final Page<Booking> bookings) {
        List<BookingDTO> bookingDTOS = bookings.stream().map(
                        booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
        return bookingDTOS;
    }

    public ByteArrayOutputStream exportBookingsToExcel(List<ReportBookingDTO> reportBookingsDTO) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bookings");

        // Định dạng tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Tên Khách Hàng", "Chuyến Đi", "Số Vé", "Ngày Đặt", "Ngày Khởi Hành", "Tổng Tiền", "Trạng Thái"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Tạo tiêu đề cột
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i); // Tự động điều chỉnh kích thước cột
        }

        // Điền dữ liệu vào bảng
        int rowNum = 1;
        for (ReportBookingDTO booking : reportBookingsDTO) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(booking.getCustomerName());
            row.createCell(1).setCellValue(booking.getTrip());
            row.createCell(2).setCellValue(booking.getTicketCount() == null ? 0 : booking.getTicketCount());
            row.createCell(3).setCellValue(booking.getBookingDate().toString());
            row.createCell(4).setCellValue(booking.getDepartureDate().toString());
            row.createCell(5).setCellValue(booking.getTicketCount() == null ? 0 : booking.getTicketCount());
            row.createCell(6).setCellValue(booking.getStatus());
        }

        // Tự động điều chỉnh kích thước cột cho các dữ liệu
        for (int i = 0; i < columnHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Xuất ra ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream; // Trả về ByteArrayOutputStream để sử dụng
    }
}
