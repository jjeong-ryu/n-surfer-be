package com.notion.nsurfer.user.controller;

import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.user.dto.DeleteUserDto;
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

    @DeleteMapping
    public ResponseEntity<ResponseDto<DeleteUserDto>> DeleteUser(@AuthenticationPrincipal User user){
        return new ResponseEntity(userService.deleteUser(user), OK);
    }

}
