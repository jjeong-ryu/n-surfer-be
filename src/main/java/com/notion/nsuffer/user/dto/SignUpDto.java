package com.notion.nsuffer.user.dto;

import com.notion.nsuffer.common.config.Authority;
import lombok.Builder;
import lombok.Getter;

public class SignUpDto {
    @Getter
    @Builder
    public static class Request {
        private String email;
        private String nickname;
        private String birthday;
        private Authority authority;
    }
}
