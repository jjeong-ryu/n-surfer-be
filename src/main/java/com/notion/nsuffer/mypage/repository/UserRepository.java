package com.notion.nsuffer.mypage.repository;

import com.notion.nsuffer.mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
