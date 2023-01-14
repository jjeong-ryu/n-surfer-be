package com.notion.nsuffer.user.service;

import com.notion.nsuffer.common.ResponseCode;
import com.notion.nsuffer.common.ResponseDto;
import com.notion.nsuffer.user.dto.SignUpDto;
import com.notion.nsuffer.user.entity.User;
import com.notion.nsuffer.user.mapper.UserMapper;
import com.notion.nsuffer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ResponseDto<Object> signUp(SignUpDto.Request request) {
        signUpValidation(request);
        User user = userMapper.signUpToUser(request);
        userRepository.save(user);
        return ResponseDto.builder()
                .responseCode(ResponseCode.SIGN_UP)
                .data(null)
                .build();
    }

    public void signUpValidation(SignUpDto.Request request){

    }
}
