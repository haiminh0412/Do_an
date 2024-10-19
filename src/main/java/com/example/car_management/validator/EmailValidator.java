package com.example.car_management.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {
    @Override
    public void initialize(EmailConstraint emailConstraint) {
        ConstraintValidator.super.initialize(emailConstraint);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        // Mẫu email hợp lệ chỉ cho phép đuôi @gmail.com
        String gmailPattern = "^[A-Za-z0-9+_.-]+@gmail\\.com$";

        // Kiểm tra email không null và khớp với mẫu đã định nghĩa
        return email != null && email.matches(gmailPattern);
    }
}
