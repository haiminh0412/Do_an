package com.example.car_management.service;

import com.example.car_management.dto.BookingDTO;
import com.example.car_management.dto.BookingInfoDTO;
import com.example.car_management.dto.response.BookingSeatResponse;
import com.example.car_management.entity.Promotion;
import com.example.car_management.model.QRCodeGenerator;
import com.example.car_management.utils.Utils;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;

    @Autowired
    private final SpringTemplateEngine templateEngine;

    @Autowired
    private final ModelMapper modelMapper;

    private static final String HOST_EMAIL = "haiminh042003@gmail.com";

    @Async
    public void sendEmail(final BookingSeatResponse bookingSeatResponse) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // Lấy các giá trị từ BookingSeatResponse
            Integer bookingId = bookingSeatResponse.getBooking().getBookingId();
            String name = bookingSeatResponse.getBooking().getCustomer().getName();
            String email = bookingSeatResponse.getBooking().getCustomer().getEmail();
            String phoneNumber = bookingSeatResponse.getBooking().getCustomer().getPhone();
            String departure = bookingSeatResponse.getBooking().getTripDetail().getTrip().getDeparture();
            String destination = bookingSeatResponse.getBooking().getTripDetail().getTrip().getDestination();
            String departureTime = bookingSeatResponse.getBooking().getTripDetail().getDepartureTime().toString();
            String destinationTime = bookingSeatResponse.getBooking().getTripDetail().getDestinationTime().toString();
            String startDestination = bookingSeatResponse.getBooking().getStartDestination();
            String endDestination = bookingSeatResponse.getBooking().getEndDestination();
            String departureDate = bookingSeatResponse.getBooking().getDepartureDate().toString();
            Long totalPrice = bookingSeatResponse.getTotalPrice();

            // Format giá tiền
            String formattedPrice = Utils.formattedPrice(totalPrice);

            // Chuyển đổi danh sách ghế thành chuỗi
            List<String> seats = bookingSeatResponse.getSeats().stream()
                    .map(seat -> seat.getSeatNumber())
                    .collect(Collectors.toList());
            String numberSeat = String.join(", ", seats);

            // Sử dụng StringBuilder để tạo nội dung QR
            StringBuilder qrContent = new StringBuilder();
            qrContent.append("Booking ID: ").append(bookingId).append("\n")
                    .append("Name: ").append(name).append("\n")
                    .append("Email: ").append(email).append("\n")
                    .append("Phone: ").append(phoneNumber).append("\n")
                    .append("Departure: ").append(departure).append("\n")
                    .append("Destination: ").append(destination).append("\n")
                    .append("Departure Time: ").append(departureTime).append("\n")
                    .append("Destination Time: ").append(destinationTime).append("\n")
                    .append("Start Destination: ").append(startDestination).append("\n")
                    .append("End Destination: ").append(endDestination).append("\n")
                    .append("Seats: ").append(numberSeat).append("\n")
                    .append("Departure Date: ").append(departureDate).append("\n")
                    .append("Total Price: ").append(formattedPrice);

            // Tạo mã QR cho nội dung đầy đủ
            byte[] qrCodeImage = QRCodeGenerator.generateQRCode(qrContent.toString());

            // Chuyển mã QR thành Base64 string
            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeImage);
            String qrCodeDataUrl = "data:image/png;base64," + qrCodeBase64;

            // Tạo context cho template
            Context context = new Context();
            context.setVariable("bookingId", bookingId);
            context.setVariable("name", name);
            context.setVariable("email", email);
            context.setVariable("phoneNumber", phoneNumber);
            context.setVariable("departure", departure);
            context.setVariable("destination", destination);
            context.setVariable("departureTime", departureTime);
            context.setVariable("destinationTime", destinationTime);
            context.setVariable("startDestination", startDestination);
            context.setVariable("endDestination", endDestination);
            context.setVariable("numberSeat", numberSeat);
            context.setVariable("departureDate", departureDate);
            context.setVariable("totalPrice", formattedPrice);
            context.setVariable("qrCodeDataUrl", qrCodeDataUrl);  // Thêm biến mã QR

            // Render nội dung từ template
            String htmlContent = templateEngine.process("email-template", context);

            // Thiết lập nội dung email
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(HOST_EMAIL);
            helper.setTo(email);
            helper.setSubject("Thông báo đặt vé xe thành công");
            helper.setText(htmlContent, true);

            // Đính kèm mã QR
            helper.addAttachment("QRCode.png", new ByteArrayResource(qrCodeImage));

            // Gửi email
            mailSender.send(mimeMessage);

            System.out.println("Email sent successfully");

        } catch (MessagingException | IOException | WriterException e) {
            e.printStackTrace();
        }
    }

    // Method gửi email thông báo đến khách hàng
    @Async
    public void sendNotificationEmail(final BookingInfoDTO bookingInfoDTO) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // Render nội dung từ template
            Context context = new Context();
            context.setVariable("name", bookingInfoDTO.getCustomerName());
            context.setVariable("email", bookingInfoDTO.getEmail());
            context.setVariable("phoneNumber", bookingInfoDTO.getPhone());
            context.setVariable("departureTime", bookingInfoDTO.getDepartureTime());
            context.setVariable("destinationTime", bookingInfoDTO.getDestinationTime());
            context.setVariable("startDestination", bookingInfoDTO.getStartDestination());
            context.setVariable("endDestination", bookingInfoDTO.getEndDestination());
            String htmlContent = templateEngine.process("notification-template", context);

            // Thiết lập nội dung email
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(HOST_EMAIL);
            helper.setTo(bookingInfoDTO.getEmail());
            helper.setSubject("Nhắc nhở chuyến đi sắp khởi hành");
            helper.setText(htmlContent, true);

            // Gửi email
            mailSender.send(mimeMessage);

            System.out.println("Notification email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendCancellationConfirmationEmail(final String cancelCode, final BookingDTO booking) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // Lấy thông tin từ booking
            String name = booking.getCustomer().getName();
            String email = booking.getCustomer().getEmail();
            String phone = booking.getCustomer().getPhone();
            String departure = booking.getTripDetail().getTrip().getDeparture();
            String destination = booking.getTripDetail().getTrip().getDestination();
            String departureTime = booking.getTripDetail().getDepartureTime().toString();
            String formattedPrice = Utils.formattedPrice(booking.getTripDetail().getPrice());

            // Tạo context cho template
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("email", email);
            context.setVariable("phone", phone);
            context.setVariable("cancelCode", cancelCode);  // Mã xác nhận hủy
            context.setVariable("departure", departure);
            context.setVariable("destination", destination);
            context.setVariable("departureTime", departureTime);
            context.setVariable("totalPrice", formattedPrice);

            // Render nội dung từ template
            String htmlContent = templateEngine.process("cancel-confirmation-template", context);

            // Thiết lập nội dung email
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(HOST_EMAIL);
            helper.setTo(email);
            helper.setSubject("Xác nhận hủy vé xe");
            helper.setText(htmlContent, true);

            // Gửi email
            mailSender.send(mimeMessage);

            System.out.println("Cancellation confirmation email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendPromotionCodeToCustomer(String email, Promotion promotion) {
        try {
            // Tạo MimeMessage cho email HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Thiết lập thông tin người nhận và tiêu đề
            helper.setTo(email);
            helper.setSubject("Mã khuyến mãi đặc biệt dành cho bạn!");

            // Sử dụng Thymeleaf để xử lý nội dung email
            Context context = new Context();
            context.setVariable("promotion", promotion);

            // Render nội dung HTML từ template
            String htmlContent = templateEngine.process("promotion_email_template", context);
            helper.setText(htmlContent, true); // true để cho biết đây là nội dung HTML

            // Gửi email
            mailSender.send(message);

            System.out.println("Email HTML đã được gửi thành công đến: " + email);
        } catch (MessagingException e) {
            System.out.println("Có lỗi xảy ra khi gửi email HTML: " + e.getMessage());
        }
    }
}
