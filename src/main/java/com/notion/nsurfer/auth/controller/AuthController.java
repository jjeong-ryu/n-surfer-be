package com.notion.nsurfer.auth.controller;

import com.notion.nsurfer.auth.dto.ReissueAccessTokenDto;
import com.notion.nsurfer.auth.dto.ReissueAccessAndRefreshTokenDto;
import com.notion.nsurfer.auth.service.AuthService;
import com.notion.nsurfer.auth.dto.AuthKakaoLoginDto;
import com.notion.nsurfer.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
        System.out.println(request.getHeader("Authorization"));
        return new ResponseEntity(authService.reissueAccessToken(request), OK);
    }

//    @GetMapping("/reissue/access-refresh-token")
//    public ResponseEntity<ResponseDto<ReissueAccessAndRefreshTokenDto.Response>> reissueAccessAndRefreshToken(
//            HttpServletRequest request
//    ){
//        return new ResponseEntity(authService.reissueAccessAndRefreshToken(request), OK);
//    }
    @GetMapping("/login/kakao")
    public ResponseEntity<ResponseDto<AuthKakaoLoginDto.Response>> kakaoLogin(@RequestParam String code,
                                                                              @RequestParam String redirectUrl){
        return new ResponseEntity(authService.kakaoLogin(code, redirectUrl), OK);
    }
}
