package com.notion.nsurfer.user.repository;

import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    User findByEmail(String email);
    Optional<User> findByusername(String username);
}
