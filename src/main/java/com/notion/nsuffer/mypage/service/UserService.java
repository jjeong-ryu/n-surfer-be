package com.notion.nsuffer.mypage.service;

import com.notion.nsuffer.mypage.entity.User;
import com.notion.nsuffer.mypage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Object getUserProfile(User user){
        return null;
    }

    public Object postUserProfile(User user){
        return null;
    }

    public Object updateUserProfile(User user){
        return null;
    }

    public Object deleteUserProfile(User user){
        userRepository.delete(user);
        return null;
    }
}
