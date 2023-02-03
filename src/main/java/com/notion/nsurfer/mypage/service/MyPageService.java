package com.notion.nsurfer.mypage.service;

import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final UserMapper userMapper;

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user) {
        final String email = user.getEmail();
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE)
                .data(userMapper.getUserProfileToResponse(userRepositoryCustom.findByEmail(email)))
                .build();
    }

    public Object postUserProfile(User user){
        return null;
    }

    @Transactional
    public ResponseDto<Object> updateUserProfile(UpdateUserProfileDto.Request dto){
        User user = userRepository.findById(dto.getId())
                .orElseThrow(UserNotFoundException::new);
        user.update(dto);
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(null).build();
    }

    public Object deleteUserProfile(User user){
        userRepository.delete(user);
        return null;
    }
}
