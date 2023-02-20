package com.notion.nsurfer.user.dto;

import com.notion.nsurfer.common.config.Authority;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
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
        private Authority userType;
        private String imgUrl;
//        private List<Wave> waves;
//
//        @Builder
//        public static class Wave {
//            private LocalDate date;
//            private Integer count;
//        }
    }
}
