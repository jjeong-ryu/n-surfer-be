package com.notion.nsurfer.security.dto;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

public class ExpiredAccessTokenDto {
    @Builder
    public static class Response {
        private String newAccessToken;
    }
}
