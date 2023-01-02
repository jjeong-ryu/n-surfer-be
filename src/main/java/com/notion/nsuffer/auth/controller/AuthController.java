package com.notion.nsuffer.auth.controller;

import com.notion.nsuffer.auth.service.AuthService;
import com.notion.nsuffer.card.dto.AuthKakaoLoginDto;
import com.notion.nsuffer.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ResponseDto<AuthKakaoLoginDto.Response>> kakaoLogin(AuthKakaoLoginDto.Request request){
        return new ResponseEntity(authService.kakaoLogin(request), OK);
    }
}
