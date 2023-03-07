package com.notion.nsurfer.mypage.controller;

import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.GetWavesDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.mypage.service.MyPageService;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/wave")
    public ResponseEntity<ResponseDto<GetWavesDto.Response>> getSurfingRecord(
            @RequestParam String nickname,
            @RequestParam Integer month
    ){
        return new ResponseEntity<>(myPageService.getWaves(nickname, month), OK);

    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<GetUserProfileDto.Response>> getUserProfile(
            @AuthenticationPrincipal User user
    ){
        return new ResponseEntity<>(myPageService.getUserProfile(user), OK);
    }
}
