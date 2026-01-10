package com.example.demo.core.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.example.demo.testsupport.TestDataFactory;

class UserTest {

    @Test
    void constructor_validatesBasicFields() {
        assertThatThrownBy(() -> new User(1L, "Ana", "Pérez", "bad-email", "password123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void profileUpdate_appliesProfileFields() {
        User existing = TestDataFactory.validUser(1L);

        User patch = new User();
        patch.setDateOfBirth(new Date(0));
        patch.setNationalId(TestDataFactory.validUruguayanId("1234567"));
        patch.setPhoneNumber("+598 91 234 567");
        patch.setAddress("Nueva dirección");
        patch.setDepartment(new Department(2L));

        existing.profileUpdate(patch);
        assertThat(existing.getAddress()).isEqualTo("Nueva dirección");
        assertThat(existing.getDepartment().getId()).isEqualTo(2L);
    }
}

