package com.example.car_management.Enum;

public enum VerificationCodeType {
    CANCEL_TICKET("Hủy vé"),
    CHANGE_TICKET("Đổi vé");

    private final String description;

    VerificationCodeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

