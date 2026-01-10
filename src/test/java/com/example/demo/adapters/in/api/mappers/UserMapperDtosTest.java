package com.example.demo.adapters.in.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.adapters.in.api.dto.UserFullDto;
import com.example.demo.adapters.in.api.dto.UserRegisterDto;
import com.example.demo.core.domain.models.Department;
import com.example.demo.core.domain.models.Organizer;
import com.example.demo.core.domain.models.Reputation;
import com.example.demo.core.domain.models.User;
import com.example.demo.testsupport.TestDataFactory;

class UserMapperDtosTest {

    @Test
    void toDomain_fromRegisterDto_mapsFieldsAndValidates() {
        UserRegisterDto dto = new UserRegisterDto("Ana", "Pérez", "ana@example.com", "password123");
        User user = UserMapperDtos.toDomain(dto);
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("ana@example.com");
        assertThat(user.getName()).isEqualTo("Ana");
        assertThat(user.getLastName()).isEqualTo("Pérez");
    }

    @Test
    void toDomain_fromFullDto_mapsDepartmentId() {
        UserFullDto dto = new UserFullDto(
                1L,
                "Ana",
                "Pérez",
                "ana@example.com",
                TestDataFactory.validUser(1L).getDateOfBirth(),
                TestDataFactory.validUser(1L).getNationalId(),
                "+598 91 234 567",
                "Calle 123",
                9L,
                0.0);
        User user = UserMapperDtos.toDomain(dto);
        assertThat(user.getDepartment()).isEqualTo(new Department(9L));
    }

    @Test
    void toFullDto_includesOrganizerReputationWhenOrganizerInstance() {
        Organizer organizer = new Organizer();
        organizer.setId(1L);
        organizer.setName("Ana");
        organizer.setLastName("Pérez");
        organizer.setEmail("ana@example.com");
        organizer.setDepartment(new Department(1L));
        organizer.setReputation(List.of(new Reputation(TestDataFactory.validUser(2L), organizer, null, 4, "ok")));

        UserFullDto dto = UserMapperDtos.toFullDto(organizer);
        assertThat(dto.getReputation()).isEqualTo(4.0);
    }
}

