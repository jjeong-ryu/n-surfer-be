package com.notion.nsurfer.user.service;

import com.notion.nsurfer.auth.utils.AuthRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.SignInDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.UserLoginInfo;
import com.notion.nsurfer.user.exception.EmailNotFoundException;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRepositoryCustom userRepositoryCustom;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public SignUpDto.Response signUpWithKakao(SignUpDto.Request request) {
        signUpValidation(request);
        User user = userMapper.signUpToUser(request);
        userRepository.save(user);
        String accessToken = JwtUtil.createAccessToken(user);
//        String refreshToken = JwtUtil.createRefreshToken(user);
        return SignUpDto.Response.builder()
                .accessToken(accessToken)
//                .refreshToken(refreshToken)
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
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("test", "1", Duration.ofHours(12));
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
        saveAccessTokenToRedis(user, accessToken);
        saveRefreshTokenToRedis(user, refreshToken);
        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user).build();
        userLoginInfoRepository.save(userLoginInfo);
        return SignUpDto.TestResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    // 추후 accessToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveAccessTokenToRedis(User user, String accessToken) {
        redisTemplate.opsForValue().set(AuthRedisKeyUtils.makeRedisAccessTokenKey(user), accessToken);
    }

    // 추후 refreshToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveRefreshTokenToRedis(User user, String refreshToken) {
        redisTemplate.opsForValue().set(AuthRedisKeyUtils.makeRedisRefreshToken(user), refreshToken);
    }
}
