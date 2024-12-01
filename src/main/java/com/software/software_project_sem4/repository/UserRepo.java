package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
     Boolean existsByUserName(String username);
     Boolean existsByEmail(String email);

     Optional<User> findById(Long userId);
     Optional<User> findByEmail(String email);
}
