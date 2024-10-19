package com.example.car_management.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, String> {
    @Override
    public void initialize(PhoneNumberConstraint contactNumber) {
        ConstraintValidator.super.initialize(contactNumber);
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext cxt) {
        // Mẫu số điện thoại hợp lệ ở Việt Nam: bắt đầu với "0" hoặc "+84", theo sau là 9 chữ số
        String vietnamPhonePattern = "^0[3|5|7|8|9][0-9]{8}$";

        // Kiểm tra số điện thoại không null và khớp với mẫu đã định nghĩa
        return phoneNumber != null && phoneNumber.matches(vietnamPhonePattern);
    }
}
