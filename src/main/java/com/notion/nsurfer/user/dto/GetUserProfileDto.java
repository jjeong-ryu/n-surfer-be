package com.notion.nsurfer.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class GetUserProfileDto {
    @Getter
    @Builder
    public static class Response {
        private Long userId;
        private String userEmail;
        private String provider;
        private String userName;
        private String userBirth;
        private String imgUrl;
        private Integer totalWave;
        private Integer todayWave;
    }
}
