package com.notion.nsurfer.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.auth.utils.AuthRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final Cloudinary cloudinary;

    @Transactional
    public SignUpDto.Response signUpWithKakao(SignUpDto.Request request) {
        signUpValidation(request);
        String randomNickname = UUID.randomUUID().toString().replace("-", "");
        User user = userMapper.signUpToUser(request, randomNickname);
        userRepository.save(user);
        return SignUpDto.Response.builder()
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .email(request.getEmail())
                .nickname(request.getNickname()).build();
    }

    public void signUpValidation(SignUpDto.Request request){
    }
    @Transactional
    public ResponseDto<Object> updateUserProfile(UpdateUserProfileDto.Request dto) throws IOException {
        User user = userRepository.findById(dto.getUserInfo().getId())
                .orElseThrow(UserNotFoundException::new);
        user.update(dto);
        MultipartFile uploadedImage = dto.getImage();
        if(uploadedImage != null){
            String imageName = StringUtils.join(List.of(dto.getUserInfo().getEmail(), dto.getUserInfo().getProvider()), "_");
            Map uploadResponse = cloudinary.uploader().upload(uploadedImage, ObjectUtils.asMap("public_id", imageName));
            user.updateImage(uploadResponse.get("url").toString());
        }
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(null).build();
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

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(String nickname){
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE)
                .data(userMapper.getUserProfileToResponse(user))
                .build();
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
