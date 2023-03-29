package com.notion.nsurfer.mypage.controller;

import com.notion.nsurfer.card.dto.PostCardDto;
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
import org.springframework.web.multipart.MultipartFile;


import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/wave")
    public ResponseEntity<ResponseDto<GetWavesDto.Response>> getSurfingRecord(
            @RequestParam String username,
            @RequestParam Integer month
    ){
        return new ResponseEntity<>(myPageService.getWaves(username, month), OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<GetUserProfileDto.Response>> getUserProfile(
            @AuthenticationPrincipal User user
    ){
        return new ResponseEntity<>(myPageService.getUserProfile(user), OK);
    }
    @PatchMapping("/profile")
    public ResponseEntity<Object> updateProfile(
            @RequestPart("updateProfile") UpdateUserProfileDto.Request dto,
            @RequestPart MultipartFile imageFile,
            @AuthenticationPrincipal User user) throws Exception {
        return new ResponseEntity<>(myPageService.updateProfile(dto, imageFile, user), OK);
    }
}
