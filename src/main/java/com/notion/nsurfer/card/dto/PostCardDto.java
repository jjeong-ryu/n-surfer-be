package com.notion.nsurfer.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class PostCardDto {

    @Getter
    public static class Request {
        private String title;
        private String content;
        private List<Label> labels;
        @Getter
        public static class Label {
            private String name;
            private String color;
        }
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private UUID cardId;
    }
}
