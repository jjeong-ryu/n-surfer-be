package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UpdateUserProfileDto {
    @Getter
    public static class Request {
        private UserInfo userInfo;
        private MultipartFile image;
        @Getter
        public static class UserInfo {
            private Long id;
            private String email;
            private String provider;
            private String nickname;
        }
    }
}
