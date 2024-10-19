package com.example.car_management.Enum;

public enum BookingStatus {
    PENDING("Pending"), // Đang chờ xử lý
    CONFIRMED("Confirmed"), // Đã xác nhận
    CANCELLED("Cancelled"), // Đã hủy
    FAILED("Failed"), // Giao dịch thất bại
    COMPLETED("Completed"), // Hoàn tất
    REFUNDED("Refunded"), // Đã hoàn tiền
    NO_SHOW("No Show"), // Không xuất hiện
    ON_HOLD("On Hold"); // Tạm giữ

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}