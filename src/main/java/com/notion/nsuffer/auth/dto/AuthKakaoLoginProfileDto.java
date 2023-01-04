package com.notion.nsuffer.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

public class AuthKakaoLoginProfileDto {
    @Getter
    @Builder
    public static class Request {
        private String code;
        @JsonProperty("redirect_uri")
        private String redirectUri;
        @Builder.Default
        @JsonProperty("grant_type")
        private String grantType = "authorization_code";
        @JsonProperty("client_id")
        private String clientId;
    }

    @Getter
    @Builder
    public static class Response {
        private String access_token;
    }

}
