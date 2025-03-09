package com.mylearning.springsecurityjwt.AppUtils;

import com.mylearning.springsecurityjwt.dto.UserDto;
import com.mylearning.springsecurityjwt.entity.User;

public class Mapper {

    public static User toUser(UserDto userDto) {
        return User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
