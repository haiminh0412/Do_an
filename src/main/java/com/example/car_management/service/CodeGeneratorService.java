package com.example.car_management.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@AllArgsConstructor
public class CodeGeneratorService {

    // Các ký tự có thể xuất hiện trong mã (chữ in hoa, chữ thường, và số)
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Sử dụng SecureRandom để đảm bảo tính ngẫu nhiên mạnh hơn Random
    private static final SecureRandom RANDOM = new SecureRandom();

    // Độ dài của mã (7 ký tự)
    private static final int CODE_LENGTH = 7;

    public String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            // Chọn ngẫu nhiên một ký tự từ CHARACTERS và thêm vào code
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }
}