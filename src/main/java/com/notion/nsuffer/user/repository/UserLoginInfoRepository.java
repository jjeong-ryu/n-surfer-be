package com.notion.nsuffer.user.repository;

import com.notion.nsuffer.user.entity.UserLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfo,String > {
    Optional<UserLoginInfo> findByEmail(String email);
}
