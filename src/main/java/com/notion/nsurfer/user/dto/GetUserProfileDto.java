package com.notion.nsurfer.user.dto;

import lombok.Builder;
import lombok.Getter;

public class GetUserProfileDto {
    @Getter
    @Builder
    public static class Response {
        private String email;
        private String nickname;
        private String thumbnailImageUrl;
    }
}
