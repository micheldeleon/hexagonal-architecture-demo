package com.tutorneo.core.domain.services;

import java.util.Date;
import java.util.regex.Pattern;

import com.tutorneo.core.domain.models.User;

public class ValidateUserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // +, espacios, paréntesis y guiones.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9 ()-]{6,20}$"
    );

    /* ===========================
       Métodos atómicos (reciben el tipo puntual)
       =========================== */

    // name
    public static void validateNameRequired(String name) {
        if (isBlank(name)) throw new IllegalArgumentException("Name is required");
    }

    // email
    public static void validateEmailRequired(String email) {
        if (isBlank(email)) throw new IllegalArgumentException("Email is required");
    }

    public static void validateEmailFormat(String email) {
        if (isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }

    // password
    public static void validatePasswordRequired(String password) {
        if (isBlank(password)) throw new IllegalArgumentException("Password is required");
    }

    public static void validatePasswordMinLength(String password, int min) {
        if (password == null || password.length() < min) {
            throw new IllegalArgumentException("Password must be at least " + min + " characters");
        }
    }

    // date of birth
    public static void validateDateOfBirthRequired(Date dob) {
        if (dob == null) throw new IllegalArgumentException("Date of birth is required");
    }

    public static void validateDateOfBirthNotFuture(Date dob) {
        if (dob != null && dob.after(new Date())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
    }

    // national id (CI Uruguay)
    public static void validateNationalIdRequired(String ci) {
        if (isBlank(ci)) throw new IllegalArgumentException("National ID (CI) is required");
    }

    public static void validateNationalIdFormat(String ci) {
        if (isBlank(ci) || !ValidateUruguayanIdService.validateUruguayanId(ci)) {
            throw new IllegalArgumentException("National ID (CI) is invalid");
        }
    }

    // phone
    public static void validatePhoneRequired(String phone) {
        if (isBlank(phone)) throw new IllegalArgumentException("Phone number is required");
    }

    public static void validatePhoneFormat(String phone) {
        if (isBlank(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Phone number format is invalid");
        }
    }

    /* ===========================
       Métodos “grandes” que reciben User
       =========================== */

    public static void validateBasic(User user) {
        if (user == null) throw new IllegalArgumentException("User must not be null");

        validateNameRequired(user.getName());

        validateEmailRequired(user.getEmail());
        validateEmailFormat(user.getEmail());

        validatePasswordRequired(user.getPassword());
        validatePasswordMinLength(user.getPassword(), 8);
    }

    public static void validateProfile(User user) {
        if (user == null) throw new IllegalArgumentException("User must not be null");

        validateDateOfBirthRequired(user.getDateOfBirth());
        validateDateOfBirthNotFuture(user.getDateOfBirth());

        validateNationalIdRequired(user.getNationalId());
        validateNationalIdFormat(user.getNationalId());

        validatePhoneRequired(user.getPhoneNumber());
        validatePhoneFormat(user.getPhoneNumber());
    }

    public static void validateAll(User user) {
        validateBasic(user);
        validateProfile(user);
    }

    /* ===========================
       Helpers
       =========================== */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
