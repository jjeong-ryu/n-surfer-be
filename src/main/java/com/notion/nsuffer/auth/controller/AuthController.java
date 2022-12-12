package com.notion.nsuffer.auth.controller;

import com.notion.nsuffer.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private AuthService authService;
}
