package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Builder
public class UpdateUserProfileDto {
    @Getter
    @Builder
    public static class Request {
        private MultipartFile image;
        private String username;
        private Boolean isBasicImage;
    }
    @Getter
    @Builder
    public static class Response {
        private Long userId;
    }
}
