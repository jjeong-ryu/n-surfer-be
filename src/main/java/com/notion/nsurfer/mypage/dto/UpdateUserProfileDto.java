package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Builder
public class UpdateUserProfileDto {
    @Getter
    public static class Request {
        private Long id;
        private String nickname;
    }
}
