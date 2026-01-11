package com.example.demo.adapters.out.persistence.jpa.mappers;

import java.util.List;

import com.example.demo.adapters.out.persistence.jpa.entities.RoleEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;
import com.example.demo.core.domain.models.User;

public class UserMapper {
    public static UserEntity toEntity(User user, List<RoleEntity> roles) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setDateOfBirth(user.getDateOfBirth());
        userEntity.setNationalId(user.getNationalId());
        userEntity.setPhoneNumber(user.getPhoneNumber());
        userEntity.setAddress(user.getAddress());
        userEntity.setProfileImageUrl(user.getProfileImageUrl());
        userEntity.setDepartment(DepartmentMapper.toEntity(user.getDepartment()));
        userEntity.setRoles(roles);
        return userEntity;
    }

    public static User toDomain(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setName(userEntity.getName());
        user.setLastName(userEntity.getLastName());
        user.setEmail(userEntity.getEmail());
        user.setPassword(userEntity.getPassword());
        user.setDateOfBirth(userEntity.getDateOfBirth());
        user.setNationalId(userEntity.getNationalId());
        user.setPhoneNumber(userEntity.getPhoneNumber());
        user.setAddress(userEntity.getAddress());
        user.setProfileImageUrl(userEntity.getProfileImageUrl());
        user.setDepartment(DepartmentMapper.toDomain(userEntity.getDepartment()));
        return user;
    }

}
