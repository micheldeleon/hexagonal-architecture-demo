package com.tutorneo.core.domain.services;

public class ValidateUruguayanIdService {
    public static boolean validateUruguayanId(String id) {
        id = id.replaceAll("\\D", ""); // remove dots and dashes
        if (id.length() != 8)
            return false;

        int[] multipliers = { 2, 9, 8, 7, 6, 3, 4 };
        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(id.charAt(i)) * multipliers[i];
        }

        int remainder = sum % 10;
        int checkDigit = remainder == 0 ? 0 : 10 - remainder;

        return checkDigit == Character.getNumericValue(id.charAt(7));
    }
}
