package com.notion.nsuffer.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthKakaoLoginTokenDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("refresh_token")
        private String refresh_token;
        @JsonProperty("expires_in")
        private int expiresIn;
        private String scope;
        @JsonProperty("refresh_token_expires_in")
        private int refreshTokenExpiresIn;
        @JsonProperty("access_token")
        private String accessToken;
    }
}
