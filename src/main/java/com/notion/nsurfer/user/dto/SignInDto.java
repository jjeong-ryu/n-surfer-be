package com.notion.nsurfer.user.dto;

import lombok.Builder;
import lombok.Getter;

public class SignInDto {
    @Getter
    @Builder
    public static class Request {
        private String email;
        private String password;
    }
}
