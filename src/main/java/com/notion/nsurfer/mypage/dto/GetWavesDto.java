package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class GetWavesDto {
    @Getter
    @Builder
    public static class Response {
        private List<Wave> waves;

        @Getter
        @Builder
        public static class Wave {
            private String date;
            private Integer count;
        }
    }
}
