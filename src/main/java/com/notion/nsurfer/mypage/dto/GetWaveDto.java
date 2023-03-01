package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWaveDto {
    @Getter
    @Builder
    public static class Response {
        private Map<String, Integer> waves;
    }
}
