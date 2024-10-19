package com.example.car_management.utils;

public class StringUtils extends org.springframework.util.StringUtils {
    public static String normalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Trim leading and trailing whitespaces
        String trimmedInput = input.trim();

        // Split the string into words
        String[] words = trimmedInput.split("\\s+");

        // Capitalize the first letter of each word and join them with a single space
        StringBuilder normalizedString = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                // Convert the first character to uppercase and the rest to lowercase
                normalizedString.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        // Remove the trailing space
        return normalizedString.toString().trim();
    }
}