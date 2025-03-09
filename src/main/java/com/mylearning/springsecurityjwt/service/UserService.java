package com.mylearning.springsecurityjwt.service;

import com.mylearning.springsecurityjwt.AppUtils.Mapper;
import com.mylearning.springsecurityjwt.dto.UserDto;
import com.mylearning.springsecurityjwt.entity.User;
import com.mylearning.springsecurityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String addUser(UserDto userDto) {
        User user = Mapper.toUser(userDto);
        try {
            userRepository.save(user);
            return "User registered successfully";
        } catch (DataIntegrityViolationException e) {
            return "User with username '" + userDto.getUsername() + "' already exists!";
        }
    }
}
