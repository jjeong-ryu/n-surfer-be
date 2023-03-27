package com.notion.nsurfer.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.notion.nsurfer.auth.common.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        @Builder.Default
        private String provider = AuthUtil.KAKAO;
        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;
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
                private String username;
                @JsonProperty("thumbnail_image_url")
                private String thumbnailImageUrl;
                @JsonProperty("profile_image_url")
                private String profileImageUrl;
            }
            private String email;
        }
    }

}
