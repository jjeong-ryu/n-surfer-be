package com.notion.nsurfer.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeleteUserDto {
    @Getter
    @Builder
    public static class Request{

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long userId;
    }
}
