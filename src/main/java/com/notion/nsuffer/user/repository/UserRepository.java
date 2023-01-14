package com.notion.nsuffer.user.repository;

import com.notion.nsuffer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
