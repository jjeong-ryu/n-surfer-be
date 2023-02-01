package com.notion.nsurfer.user.controller;

import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Object> signUp(SignUpDto.Request request){
        return new ResponseEntity(userService.signUp(request), OK);
    }

}
