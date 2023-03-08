package com.notion.nsurfer.auth.service;

import com.notion.nsurfer.auth.dto.*;
import com.notion.nsurfer.auth.utils.AuthRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.security.VerifyResult;
import com.notion.nsurfer.security.exception.ExpiredJwtTokenException;
import com.notion.nsurfer.security.exception.InvalidJwtException;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.UserLoginInfo;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static com.notion.nsurfer.auth.common.AuthUtil.*;
import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO_ACCESS_TOKEN_REQUEST_URL;
import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO_PROFILE_REQUEST_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final RedisTemplate<String, String> redisTemplate;
    public ResponseDto<AuthKakaoLoginDto.Response> kakaoLogin(final String code, final String redirectUrl) {
        final String kakaoAccessToken = getKakaoAccessToken(code, redirectUrl, KAKAO);
        AuthKakaoLoginProfileDto.Response userProfile = getKaKaoUserprofile(kakaoAccessToken);
        User user = findUserByEmailAndProvider(userProfile, KAKAO);
        if(user != null) {
            return this.kakaoLoginWithoutSignUp(user);
        }
        return this.kakaoLoginWithSignUp(userProfile);
    }

    private User findUserByEmailAndProvider(AuthKakaoLoginProfileDto.Response userProfile, final String kakao) {
        return userRepository.findByEmailAndProvider(userProfile.getKakaoAccount().getEmail(),
                kakao).orElse(null);
    }

    private ResponseDto<AuthKakaoLoginDto.Response> kakaoLoginWithoutSignUp(User user) {
        String accessToken = JwtUtil.createAccessToken(user);
        String refreshToken = JwtUtil.createRefreshToken(user);
        return getKaKaoLoginResponse(user, accessToken, refreshToken);
    }

    private ResponseDto<AuthKakaoLoginDto.Response> kakaoLoginWithSignUp(AuthKakaoLoginProfileDto.Response userprofile) {
        signUpWithKakao(userprofile);
        User user = findUserByEmailAndProvider(userprofile, KAKAO);
        String accessToken = JwtUtil.createAccessToken(user);
        String refreshToken = JwtUtil.createRefreshToken(user);
    // 처음 회원가입 하는 경우
        return getKaKaoLoginResponse(user, accessToken, refreshToken);
    }

    private static ResponseDto<AuthKakaoLoginDto.Response> getKaKaoLoginResponse(User user, String accessToken, String refreshToken) {
        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .responseCode(ResponseCode.SIGN_IN)
                .data(AuthKakaoLoginDto.Response.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .thumbnailImageUrl(user.getThumbnailImageUrl())
                        .email(user.getEmail())
                        .nickname(user.getNickname()).build())
                .build();
    }

    private String getKakaoAccessToken(final String code, final String redirectUrl, final String provider) {
        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_ACCESS_TOKEN_REQUEST_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        MultiValueMap<String, String> profileRequest = makeProfileRequest(code, redirectUrl, provider);
        AuthKakaoLoginTokenDto.Response profileResponse = webClient
                .post()
                .bodyValue(profileRequest)
                .retrieve()
                .bodyToMono(AuthKakaoLoginTokenDto.Response.class)
                .block();
        return profileResponse.getAccessToken();
    }

    private MultiValueMap<String, String> makeProfileRequest(String code, String redirectUrl, String provider) {
        MultiValueMap<String, String> profileRequest = new LinkedMultiValueMap<>();
        profileRequest.add("grant_type", "authorization_code");
        profileRequest.add("code", code);
        profileRequest.add("redirect_url", redirectUrl);
        profileRequest.add("client_id", getClientId(provider));
        return profileRequest;
    }

    private AuthKakaoLoginProfileDto.Response getKaKaoUserprofile(final String kakaoAccessToken){
        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_PROFILE_REQUEST_URL)
                .defaultHeader("Authorization", "Bearer " + kakaoAccessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        return webClient.get()
                .retrieve()
                .bodyToMono(AuthKakaoLoginProfileDto.Response.class)
                .block();
    }
    private SignUpDto.Response signUpWithKakao(AuthKakaoLoginProfileDto.Response userprofile
    ){
        SignUpDto.Request signUpRequest = userMapper.signUpKakaoToRequest(userprofile);
        return userService.signUpWithKakao(signUpRequest);
    }


//    private void signUpWithGoogle(AuthKakaoLoginProfileDto.Response userprofile){
//        userService.signUp(userMapper.signUpKakaoToRequest(userprofile));
//    }


    @Transactional
    public ResponseDto<ReissueAccessTokenDto.Response> reissueAccessToken(
            HttpServletRequest request
    ) throws IOException {
        VerifyResult verifyResult;
        try {
            verifyResult = verifyToken(request);
        } catch (ExpiredJwtTokenException expiredRefreshTokenException) {
            return makeExpiredRefreshTokenResponse();
        } catch (InvalidJwtException invalidAccessTokenException) {
            return makeInvalidRefreshTokenResponse();
        }
        String newAccessToken = makeNewAccessToken(verifyResult.getEmailAndProvider());
        return ResponseDto.<ReissueAccessTokenDto.Response>builder()
                .responseCode(ResponseCode.MAKE_NEW_ACCESS_TOKEN)
                .data(ReissueAccessTokenDto.Response.builder()
                        .accessToken(newAccessToken).build())
                .build();
    }
    private ResponseDto<ReissueAccessTokenDto.Response> makeInvalidRefreshTokenResponse() throws IOException {
        return ResponseDto.<ReissueAccessTokenDto.Response>builder()
            .responseCode(ResponseCode.ERROR_INVALID_REFRESH_TOKEN)
            .data(null).build();

    }
    private ResponseDto<ReissueAccessTokenDto.Response> makeExpiredRefreshTokenResponse() throws IOException {
        return ResponseDto.<ReissueAccessTokenDto.Response>builder()
                .responseCode(ResponseCode.ERROR_EXPIRED_REFRESH_TOKEN)
                .data(null).build();

    }
//    @Transactional
//    public ResponseDto<ReissueAccessAndRefreshTokenDto.Response> reissueAccessAndRefreshToken(
//            HttpServletRequest request
//    ){
//        VerifyResult verifyResult = JwtUtil.validateToken(request.getHeader("Authorization").substring(7));
//        String emailAndProvider = verifyResult.getEmailAndProvider();
//        String newAccessToken = makeNewAccessToken(emailAndProvider);
//        String newRefreshToken = makeNewRefreshToken(emailAndProvider);
////        User user = getUserFromEmailAndProvider(verifyResult.getEmailAndProvider());
////        UserLoginInfo userLoginInfo = getUserInfoFromUser(user);
////        userLoginInfo.updateAccessToken(newAccessToken);
////        userLoginInfo.updateRefreshToken(newRefreshToken);
//        return ResponseDto.<ReissueAccessAndRefreshTokenDto.Response>builder()
//                .responseCode(ResponseCode.MAKE_NEW_ACCESS_AND_REFRESH_TOKEN)
//                .data(ReissueAccessAndRefreshTokenDto.Response.builder()
//                        .accessToken(newAccessToken)
//                        .refreshToken(newRefreshToken)
//                        .build())
//                .build();
//    }

    private String makeNewAccessToken(final String emailAndProvider){
        User user = getUserFromEmailAndProvider(emailAndProvider);
        String accessToken = JwtUtil.createAccessToken(user);
        return accessToken;
    }

    private String makeNewRefreshToken(String emailAndProvider){
        User user = getUserFromEmailAndProvider(emailAndProvider);
        String refreshToken = JwtUtil.createRefreshToken(user);
        return refreshToken;
    }
    private User getUserFromEmailAndProvider(String emailAndProvider){
        String email = emailAndProvider.split("_")[0];
        String provider = emailAndProvider.split("_")[1];
        System.out.println(email + " " + provider);
        return userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(UserNotFoundException::new);
    }

    private UserLoginInfo getUserInfoFromUser(User user){
        return userLoginInfoRepository.findByUser(user)
                .orElseThrow(UserNotFoundException::new);
    }

    // 추후 accessToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveAccessTokenToRedis(User user, String accessToken) {
        redisTemplate.opsForValue().set(AuthRedisKeyUtils.makeRedisAccessTokenKey(user), accessToken);
    }

    // 추후 refreshToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveRefreshTokenToRedis(User user, String refreshToken) {
        redisTemplate.opsForValue().set(AuthRedisKeyUtils.makeRedisRefreshToken(user), refreshToken);
    }

    private VerifyResult verifyToken(HttpServletRequest request){
        String token = JwtUtil.resolveToken(request);
//        return JwtUtil.validateTokenWithRedis(accessToken);
        return JwtUtil.validateToken(token);
    }
}
