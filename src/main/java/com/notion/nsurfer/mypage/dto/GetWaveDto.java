package com.notion.nsurfer.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class GetWaveDto {
    @Getter
    @Builder
    public static class Response {
        private List<Wave> waves;

        public static class Wave {
            
        }
    }
}
