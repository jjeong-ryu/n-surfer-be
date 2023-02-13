package com.notion.nsurfer.user.service;

import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.SignInDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.exception.EmailNotFoundException;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRepositoryCustom userRepositoryCustom;


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

    @Transactional
    public ResponseDto<DeleteUserDto.Response> deleteUser(User user) {
        User findUser = userRepository.findByEmailAndProvider(user.getEmail(), user.getProvider())
                .orElseThrow(EmailNotFoundException::new);
        findUser.delete();
        return ResponseDto.<DeleteUserDto.Response>builder()
                .responseCode(ResponseCode.DELETE_USER)
                .data(userMapper.deleteUserToResponse(findUser))
                .build();
    }

    @Transactional
    public String localSignUpForTest(SignUpDto.Request request){
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password("1234")
                .thumbnailImageUrl("hi")
                .provider(KAKAO)
                .build();
        userRepository.save(user);
        return "good";
    }
    public SignUpDto.TestResponse localSignInForTest(SignInDto.Request request){
        User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword()).get();
        String accessToken = JwtUtil.createAccessToken(user);
        String refreshToken = JwtUtil.createRefreshToken(user);
        return SignUpDto.TestResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }
}
