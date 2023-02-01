package com.notion.nsurfer.user.service;

import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.exception.EmailNotFoundException;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRepositoryCustom userRepositoryCustom;

    @Transactional
    public ResponseDto<Object> signUp(SignUpDto.Request request) {
        signUpValidation(request);
        User user = userMapper.signUpToUser(request);
        userRepository.save(user);
        return ResponseDto.builder()
                .responseCode(ResponseCode.SIGN_UP)
                .data(null)
                .build();
    }

    @Transactional
    public SignUpDto.Response signUpWithKakao(SignUpDto.Request request) {
        signUpValidation(request);
        User user = userMapper.signUpToUser(request);
        userRepository.save(user);
        String accessToken = JwtUtil.createAccessToken(user);
        return SignUpDto.Response.builder()
                .accessToken(accessToken)
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .email(request.getEmail())
                .nickname(request.getNickname()).build();
    }

    public void signUpValidation(SignUpDto.Request request){
    }

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user) {
        final String email = user.getEmail();
        User findUser = userRepositoryCustom.findByEmail(email);
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE)
                .data(userMapper.getUserProfileToResponse(userRepositoryCustom.findByEmail(email)))
                .build();
    }
}
