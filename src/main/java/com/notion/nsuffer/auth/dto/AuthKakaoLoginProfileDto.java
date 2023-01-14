package com.notion.nsuffer.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AuthKakaoLoginProfileDto {
    @Getter
    @Builder
    public static class Request {
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long id;
        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;
        private String provider;
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class KakaoAccount {
            private Profile profile;
            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Profile {
                private String nickname;
                @JsonProperty("thumbnail_image_url")
                private String thumbnailImageUrl;
                @JsonProperty("profile_image_url")
                private String profileImageUrl;
            }
            private String email;
        }
    }

}
