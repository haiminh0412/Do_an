package com.example.car_management.exception;

public enum ErrorCode{
    ERROR_TYPE(300, "Kieu du lieu khong hop le"),
    EMPTY(301, "Khong duoc de trong"),
    NOT_FOUND_ID(401, "Khong tim thay id"),
    NULL(402, "Gia tri null"),
    EXIST(403, "Da ton tai"),
    NOT_EXIST(404, "Khong ton tai"),
    NOT_POSITIVE(405, "Yeu cau so nguyen duong"),
    BEFORE_DATE_NOW(406, "Du lieu ngay di nho hon ngay hien tai"),
    EXCEED_SEAT(407, "Chi duoc chon toi da 4 ghe"),
    INVALID_PHONE(408, "So dien thoai khong hop le!"),
    INVALID_EMAIL(409, "email khong hop le!"),
    INVALID_VERIFICATION_CODE(409, "Ma xac nhan da het han hoac khong hop le!"),
    INVALID_SEATS(410, "So ghe ngoi phai lon hon hoac bang 4"),
    INVALID_PRICE(411, "Gia phai lon hon 0"),
    OVERLAPPING_SCHEDULE(412, "Trung thoi gian chay"),
    INVALID_VIETNAM_LICENSE(413, "Bien so xe khong hop le"),
    INVALID_PROMOION(414, "Ma khuyen mai khong hop le"),
    INVALID_PROMOION_DATE(415, "Ngay bat dau phai nho hon ngay ket thuc")
    ;
    private int code;
    private String message;

    ErrorCode() {}

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
