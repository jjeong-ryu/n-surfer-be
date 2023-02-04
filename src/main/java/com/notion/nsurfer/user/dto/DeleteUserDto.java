package com.notion.nsurfer.user.dto;

import lombok.Builder;
import lombok.Getter;

public class DeleteUserDto {
    @Getter
    @Builder
    public static class Request{

    }

    @Builder
    public static class Response {
        private Long userId;
    }
}
