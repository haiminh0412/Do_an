package com.example.car_management.Enum;

public enum NotificationType {
    ONE_DAY("1 day"), // gui truoc 1 ngay
    ONE_HOURS("1 hours"), // gui truoc 1 tieng
    TWO_HOURS("2 hours")
    ; // gui truoc 2 tieng

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
