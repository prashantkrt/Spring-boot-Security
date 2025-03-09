package com.mylearning.springsecurityjwt.service;


import com.mylearning.springsecurityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

//Second way using user class which is the implementation of UserDetails
@Service
@RequiredArgsConstructor
public class MyCustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found!!"));
        return new User(userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found!!")).getUsername(),
                userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found!!")).getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found!!")).getRole())));
    }
}
