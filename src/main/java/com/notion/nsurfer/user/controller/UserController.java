package com.notion.nsurfer.user.controller;

import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignInDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @DeleteMapping
    public ResponseEntity<ResponseDto<DeleteUserDto>> DeleteUser(@AuthenticationPrincipal User user){
        return new ResponseEntity(userService.deleteUser(user), OK);
    }

    @PostMapping
    public String localSignUpForTest(@RequestBody SignUpDto.Request request){
        return userService.localSignUpForTest(request);
    }
    @PostMapping("/login")
    public SignUpDto.TestResponse loginForTest(@RequestBody SignInDto.Request request){
        return userService.localSignInForTest(request);
    }
    @PatchMapping("/profile")
    public ResponseEntity<Object> updateUserProfile(
            @RequestPart UpdateUserProfileDto.Request dto) throws IOException {
        return new ResponseEntity<>(userService.updateUserProfile(dto), OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<GetUserProfileDto.Response>> getUserProfile(
            @RequestPart String nickname
    ){
        return new ResponseEntity<>(userService.getUserProfile(nickname), OK);
    }
}
