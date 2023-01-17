package com.notion.nsurfer.auth.controller;

import com.notion.nsurfer.auth.service.AuthService;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginDto;
import com.notion.nsurfer.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @GetMapping("/login/kakao")
    public ResponseEntity<ResponseDto<AuthKakaoLoginDto.Response>> kakaoLogin(@RequestParam String code,
                                                                              @RequestParam String redirectUrl){
        return new ResponseEntity(authService.kakaoLogin(code, redirectUrl), OK);
    }
}
