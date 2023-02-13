package com.notion.nsurfer.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class ReissueAccessTokenDto {
    @Getter
    @Builder
    public static class Request {
        private String refreshToken;
    }

    @Builder
    public static class Response {
        private String accessToken;
    }
}
