package com.notion.nsurfer.user.dto;

import com.notion.nsurfer.common.config.Authority;
import lombok.Builder;
import lombok.Getter;

public class SignUpDto {
    @Getter
    @Builder
    public static class Request {
        private String email;
        private String provider;
        private String nickname;
        @Builder.Default
        private Authority authority = Authority.USER;
        private String thumbnailImageUrl;
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
