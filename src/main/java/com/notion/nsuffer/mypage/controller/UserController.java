package com.notion.nsuffer.mypage.controller;

import com.notion.nsuffer.mypage.entity.User;
import com.notion.nsuffer.mypage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(userService.getUserProfile(user));
    }
    @PostMapping("/profile")
    public ResponseEntity<Object> postUserProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(userService.postUserProfile(user));
    }
    @PatchMapping("/profile")
    public ResponseEntity<Object> updateUserProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(userService.updateUserProfile(user));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Object> deleteUserProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(userService.deleteUserProfile(user));
    }
}
