package com.notion.nsurfer.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.auth.utils.AuthRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.mypage.utils.MyPageRedisKeyUtils;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignInDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.UserLoginInfo;
import com.notion.nsurfer.user.exception.EmailNotFoundException;
import com.notion.nsurfer.user.exception.UsernameAlreadyExistException;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Transactional
    public SignUpDto.Response signUpWithKakao(SignUpDto.Request request) {
        String randomusername = UUID.randomUUID().toString().replace("-", "").substring(8);
        User user = userMapper.signUpToUser(request, randomusername);
        userRepository.save(user);
        return SignUpDto.Response.builder()
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .email(request.getEmail())
                .username(request.getUsername()).build();
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
        User user = User.builder()
                .username(request.getUsername())
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

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(String username){
        User user = userRepository.findByusername(username)
                .orElseThrow(UserNotFoundException::new);
        Integer totalWave = getTotalWaves(user);
        Integer todayWave = getTodayWave(user);
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE)
                .data(userMapper.getUserProfileToResponse(user, totalWave, todayWave))
                .build();
    }
    private Integer getTotalWaves(User user) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String total = opsForHash.get(redisWavesKey, "total");
        return total != null ? Integer.valueOf(total) : 0;
    }

    private Integer getTodayWave(User user) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String redisWaveHashKey = waveDateFormat.format(new Date());
        String todayWave = opsForHash.get(redisWavesKey, redisWaveHashKey);
        return todayWave != null ? Integer.valueOf(todayWave) : 0;
    }
    // 추후 accessToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveAccessTokenToRedis(User user, String accessToken) {
        redisTemplate.opsForValue().set(AuthRedisKeyUtils.makeRedisAccessTokenKey(user), accessToken);
    }

    // 추후 refreshToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveRefreshTokenToRedis(User user, String refreshToken) {
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(user.getId()));
    }
}
