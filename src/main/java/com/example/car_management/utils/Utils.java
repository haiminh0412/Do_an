package com.example.car_management.utils;

import java.text.DecimalFormat;

public class Utils {
    public static int sqrt(int x) {
        long num;
        int left = 0, right = x;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            num = (long) mid * mid;

            if (num == x) {
                return mid;
            }
            if (num > x) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return right;
    }

    public static String formattedPrice(Long price) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(price);
        return formattedPrice;
    }
}