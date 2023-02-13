package com.notion.nsurfer.user.repository;

import com.notion.nsurfer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<User> findByEmailAndPassword(String email, String password);
}
