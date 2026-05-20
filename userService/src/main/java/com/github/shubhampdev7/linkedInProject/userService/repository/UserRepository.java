package com.github.shubhampdev7.linkedInProject.userService.repository;

import com.github.shubhampdev7.linkedInProject.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
