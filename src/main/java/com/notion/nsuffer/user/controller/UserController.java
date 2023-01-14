package com.notion.nsuffer.user.controller;

import com.notion.nsuffer.user.dto.SignUpDto;
import com.notion.nsuffer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
