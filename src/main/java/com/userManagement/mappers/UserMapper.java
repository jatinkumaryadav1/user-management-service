package com.userManagement.mappers;

import com.userManagement.RoleEnum;
import com.userManagement.dtos.UserDto;
import com.userManagement.entity.User;

import java.util.ArrayList;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        // to ensure password, we can remove this
        // dto.setPassword(user.getPassword());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());

        dto.setRoles(user.getRoles());

        return dto;
    }

    // dto to entity method
    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        user.setRoles(dto.getRoles());

        return user;
    }
}
