package com.notion.nsurfer.auth.service;

import com.notion.nsurfer.auth.common.AuthUtil;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginDto;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginProfileDto;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginTokenDto;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

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
    public ResponseDto<AuthKakaoLoginDto.Response> kakaoLogin(final String code, final String redirectUrl) {
        final String kakaoAccessToken = getKakaoAccessToken(code, redirectUrl, KAKAO);
        AuthKakaoLoginProfileDto.Response userprofile = getKaKaoUserprofile(kakaoAccessToken);
        User user = userRepository.findByEmailAndProvider(userprofile.getKakaoAccount().getEmail(),
                KAKAO).orElse(null);
        if(user != null) {
            return this.loginWithoutSignUp(user, userprofile);
        }
        return this.loginWithSignUp(userprofile);
    }

    private ResponseDto<AuthKakaoLoginDto.Response> loginWithoutSignUp(User user, AuthKakaoLoginProfileDto.Response userprofile) {
        String accessToken = JwtUtil.createAccessToken(user);
        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .responseCode(ResponseCode.SIGN_UP)
                .data(AuthKakaoLoginDto.Response.builder()
                        .accessToken(accessToken)
                        .thumbnailImageUrl(userprofile.getKakaoAccount().getProfile().getThumbnailImageUrl())
                        .email(userprofile.getKakaoAccount().getEmail())
                        .nickname(userprofile.getKakaoAccount().getProfile().getNickname()).build())
                .build();
    }

    private ResponseDto<AuthKakaoLoginDto.Response> loginWithSignUp(AuthKakaoLoginProfileDto.Response userprofile) {
        SignUpDto.Response response = signUpWithKakao(userprofile);
    // 처음 회원가입 하는 경우
        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .responseCode(ResponseCode.SIGN_UP)
                .data(AuthKakaoLoginDto.Response.builder()
                        .accessToken(response.getAccessToken())
                        .thumbnailImageUrl(response.getThumbnailImageUrl())
            .email(response.getEmail())
            .nickname(response.getNickname()).build())
            .build();
    }
    private String getKakaoAccessToken(final String code, final String redirectUrl, final String provider) {
        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_ACCESS_TOKEN_REQUEST_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        MultiValueMap<String, String> profileRequest = new LinkedMultiValueMap<>();
        profileRequest.add("grant_type", "authorization_code");
        profileRequest.add("code", code);
        profileRequest.add("redirect_url", redirectUrl);
        profileRequest.add("client_id", getClientId(provider));

        AuthKakaoLoginTokenDto.Response profileResponse = webClient
                .post()
                .bodyValue(profileRequest)
                .retrieve()
                .bodyToMono(AuthKakaoLoginTokenDto.Response.class)
                .block();
        return profileResponse.getAccessToken();
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
    private SignUpDto.Response signUpWithKakao(AuthKakaoLoginProfileDto.Response userprofile){
        SignUpDto.Request signUpRequest = userMapper.signUpKakaoToRequest(userprofile);
        return userService.signUpWithKakao(signUpRequest);
        //
    }
//    private void signUpWithGoogle(AuthKakaoLoginProfileDto.Response userprofile){
//        userService.signUp(userMapper.signUpKakaoToRequest(userprofile));
//    }
}
