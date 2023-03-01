package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public class GetWavesDto {
    @Getter
    @Builder
    public static class Response {
        private Map<String, Integer> waves;
    }
}
