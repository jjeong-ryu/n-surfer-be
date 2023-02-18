package com.notion.nsurfer.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReissueAccessAndRefreshTokenDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class Response {
        private String refreshToken;
        private String accessToken;
    }
}
