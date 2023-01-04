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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AuthService {
    public ResponseDto<AuthKakaoLoginDto.Response> kakaoLogin(final String code, final String redirectUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://kauth.kakao.com/oauth/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        MultiValueMap<String, String> profileRequest = makeProfileRequest(code, redirectUrl, AuthUtil.KAKAO);
        AuthKakaoLoginProfileDto.Response profileResponse = webClient
                .post()
                .bodyValue(profileRequest)
                .retrieve()
                .bodyToMono(AuthKakaoLoginProfileDto.Response.class)
                .block();
        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .data(AuthKakaoLoginDto.Response.builder()
                        .accessToken(code)
                        .build())
                .build();
    }

    private MultiValueMap<String, String> makeProfileRequest(String code, String redirectUrl, String provider) {
        MultiValueMap<String, String> profileRequest = new LinkedMultiValueMap<>();
        profileRequest.add("grant_type", "authorization_code");
        profileRequest.add("code", code);
        profileRequest.add("redirect_url", redirectUrl);
        profileRequest.add("client_id", AuthUtil.getClientId(provider));
        return profileRequest;
    }
}
