package com.example.demo.core.domain.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.example.demo.core.domain.models.User;
import com.example.demo.testsupport.TestDataFactory;

class ValidateUserServiceTest {

    @Test
    void validateBasic_rejectsNullUser() {
        assertThatThrownBy(() -> ValidateUserService.validateBasic(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User must not be null");
    }

    @Test
    void validateBasic_rejectsInvalidEmailAndShortPassword() {
        User user = new User();
        user.setName("Ana");
        user.setEmail("not-an-email");
        user.setPassword("123");

        assertThatThrownBy(() -> ValidateUserService.validateBasic(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void validateProfile_rejectsFutureDobAndInvalidNationalIdAndPhone() {
        User patch = new User();
        patch.setDateOfBirth(new Date(System.currentTimeMillis() + 86400000L));
        patch.setNationalId("123"); // invalid
        patch.setPhoneNumber("abc");

        assertThatThrownBy(() -> ValidateUserService.validateProfile(patch))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date of birth cannot be in the future");
    }

    @Test
    void validateAll_acceptsValidUser() {
        User user = TestDataFactory.validUser(1L);
        ValidateUserService.validateAll(user);
    }
}

