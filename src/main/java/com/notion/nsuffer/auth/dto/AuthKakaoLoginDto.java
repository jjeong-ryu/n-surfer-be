package com.notion.nsuffer.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthKakaoLoginDto {
    @Getter
    public static class Request {
        private String code;
        private String redirectUrl;
    }
    @Getter
    @Builder
    public static class Response {
        private String accessToken;
    }
}
