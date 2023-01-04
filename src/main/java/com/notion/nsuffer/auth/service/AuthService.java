package com.notion.nsuffer.auth.service;

import com.notion.nsuffer.auth.common.AuthUtil;
import com.notion.nsuffer.auth.dto.AuthKakaoLoginDto;
import com.notion.nsuffer.auth.dto.AuthKakaoLoginProfileDto;
import com.notion.nsuffer.common.ResponseCode;
import com.notion.nsuffer.common.ResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AuthService {
    public ResponseDto<AuthKakaoLoginDto.Response> kakaoLogin(final String code, final String redirectUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://kauth.kakao.com/oauth/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        AuthKakaoLoginProfileDto.Request profileRequest = AuthKakaoLoginProfileDto.Request.builder()
                        .code(code).redirectUri(redirectUrl).clientId(AuthUtil.KAKAO_CLIENT_ID)
                        .build();
        AuthKakaoLoginProfileDto.Response profileResponse = webClient
                .post()
                .body(profileRequest, AuthKakaoLoginProfileDto.Request.class)
                .retrieve()
                .bodyToMono(AuthKakaoLoginProfileDto.Response.class)
                .block();

        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .data(AuthKakaoLoginDto.Response.builder()
                        .accessToken(code)
                        .build())
                .build();
    }
}
