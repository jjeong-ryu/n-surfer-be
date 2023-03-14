package com.notion.nsurfer.auth.controller;

import com.notion.nsurfer.auth.dto.ReissueAccessTokenDto;
import com.notion.nsurfer.auth.service.AuthService;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginDto;
import com.notion.nsurfer.auth.utils.AuthRedisKeyUtils;
import com.notion.nsurfer.card.exception.CardNotFoundException;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/reissue/access-token")
    public ResponseEntity<ResponseDto<ReissueAccessTokenDto.Response>> reissueAccessToken(
            HttpServletRequest request
    ) throws IOException {
        return new ResponseEntity(authService.reissueAccessToken(request), OK);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<ResponseDto<AuthKakaoLoginDto.Response>> kakaoLogin(@RequestParam String code,
                                                                              @RequestParam String redirectUrl){
        return new ResponseEntity(authService.kakaoLogin(code, redirectUrl), OK);
    }
    @GetMapping("/test")
    public void test(){
        throw new CardNotFoundException();
    }
}
