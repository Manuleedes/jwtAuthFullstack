package com.Lidigu.backend.mappers;

import com.Lidigu.backend.dtos.SignUpDto;
import com.Lidigu.backend.dtos.UserDto;
import com.Lidigu.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
@Mapping(target = "password",ignore = true)
    User signUpToUser(SignUpDto signUpDto);
}
