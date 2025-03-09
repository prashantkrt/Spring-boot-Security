package com.mylearning.springsecurityjwt.service;

import com.mylearning.springsecurityjwt.AppUtils.Mapper;
import com.mylearning.springsecurityjwt.dto.UserDto;
import com.mylearning.springsecurityjwt.entity.User;
import com.mylearning.springsecurityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public String addUser(UserDto userDto) {
        User user = Mapper.toUser(userDto);
        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return "User registered successfully";
        } catch (DataIntegrityViolationException e) {
            return "User with username '" + userDto.getUsername() + "' already exists!";
        }
    }

    public String verifyUser(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername()).orElse(null);
        if (user != null && passwordEncoder.matches(userDto.getPassword(), user.getPassword())) { // passwordEncoder.matches(rawPassword, encodedPassword
            return "User verified successfully";
        } else {
            return "Invalid username or password";
        }
    }
}
