package com.example.demo.core.domain.models;

import java.util.Date;
import java.util.List;

import com.example.demo.core.domain.services.ValidateUserService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name; // nombre
    private String lastName; // apellido
    private String email;
    private String password;
    private String googleSub;
    private Date dateOfBirth; // fechaNacimiento
    private String nationalId; // ci
    private String phoneNumber; // celular
    private String address;
    private Department department;
    private String profileImageUrl; // URL de la imagen de perfil
    private List<Tournament> tournaments;
    private List<Registration> registrations;

    public User(Long id, String name, String lastName, String email, String password) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        // this.dateOfBirth = new Date();
        this.phoneNumber = "";
        this.address = "";
        ValidateUserService.validateBasic(this);
    }

    public void profileUpdate(User patch) {
        ValidateUserService.validateProfile(patch);
        setNationalId(patch.getNationalId());
        setDateOfBirth(patch.getDateOfBirth());
        setPhoneNumber(patch.getPhoneNumber());
        setAddress(patch.getAddress());
        setDepartment(patch.getDepartment());
        if (patch.getProfileImageUrl() != null) {
            setProfileImageUrl(patch.getProfileImageUrl());
        }
    }

    public User(Long id,
            String name,
            String lastName,
            String email,
            Date dateOfBirth,
            String nationalId,
            String phoneNumber,
            String address,
            Department department) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.department = department;
    }

}
