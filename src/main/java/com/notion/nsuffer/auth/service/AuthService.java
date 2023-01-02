package com.notion.nsuffer.auth.service;

import com.notion.nsuffer.card.dto.AuthKakaoLoginDto;
import com.notion.nsuffer.common.ResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {

    public ResponseDto<AuthKakaoLoginDto.Response> kakaoLogin(AuthKakaoLoginDto.Request request) {
        String token = request.getToken();
        // request에서 정보를 추출하여 kakao로 유저 정보 요청
//        WebClient webClient = WebClient.builder()
////                .baseUrl()
////                ..build();
        return ResponseDto.<AuthKakaoLoginDto.Response>builder()
                .build();
    }
}
