package com.notion.nsuffer.card.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthKakaoLoginDto {
    @Getter
    public static class Request {
        private String token;
    }
    @Builder
    public static class Response {

    }
}
