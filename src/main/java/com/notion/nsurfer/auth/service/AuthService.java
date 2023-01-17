package com.notion.nsurfer.auth.service;

import com.notion.nsurfer.auth.common.AuthUtil;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginDto;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginProfileDto;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginTokenDto;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO_ACCESS_TOKEN_REQUEST_URL;
import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO_PROFILE_REQUEST_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserMapper userMapper;
    public ResponseDto<AuthKakaoLoginDto.Response> kakaoLogin(final String code, final String redirectUrl) {
        String kakaoAccessToken = getKakaoAccessToken(code, redirectUrl, AuthUtil.KAKAO);
        AuthKakaoLoginProfileDto.Response userprofile = getKaKaoUserprofile(kakaoAccessToken);
        System.out.println(userprofile.getKakaoAccount().getProfile().getThumbnailImageUrl());
        SignUpDto.Response response = signUpWithKakao(userprofile);
        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .responseCode(ResponseCode.SIGN_UP)
                .data(AuthKakaoLoginDto.Response.builder()
                        .accessToken(code)
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
        profileRequest.add("client_id", AuthUtil.getClientId(provider));

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
