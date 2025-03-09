package com.mylearning.springsecurityjwt.repository;

import com.mylearning.springsecurityjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
