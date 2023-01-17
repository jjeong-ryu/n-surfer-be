package com.notion.nsurfer.auth.dto;

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
        private String email;
        private String nickname;
        private String thumbnailImageUrl;
    }
}
