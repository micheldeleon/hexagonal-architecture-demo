package com.example.demo.core.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.example.demo.testsupport.TestDataFactory;

class ValidateUruguayanIdServiceTest {

    @Test
    void validateUruguayanId_acceptsValidIdsIncludingWithFormatting() {
        String id = TestDataFactory.validUruguayanId("1234567");
        assertThat(ValidateUruguayanIdService.validateUruguayanId(id)).isTrue();
        assertThat(ValidateUruguayanIdService.validateUruguayanId(id.substring(0, 1) + "." + id.substring(1, 4) + "."
                + id.substring(4, 7) + "-" + id.substring(7))).isTrue();
    }

    @Test
    void validateUruguayanId_rejectsInvalidIds() {
        String valid = TestDataFactory.validUruguayanId("7654321");
        String invalid = valid.substring(0, 7) + ((Character.getNumericValue(valid.charAt(7)) + 1) % 10);
        assertThat(ValidateUruguayanIdService.validateUruguayanId(invalid)).isFalse();
        assertThat(ValidateUruguayanIdService.validateUruguayanId("")).isFalse();
        assertThat(ValidateUruguayanIdService.validateUruguayanId("123")).isFalse();
    }
}

