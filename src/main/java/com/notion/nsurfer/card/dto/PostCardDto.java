package com.notion.nsurfer.card.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PostCardDto {

    @Getter
    public static class Request {
        private String name;
        private String content;
        private List<Label> labels;
        @Getter
        public static class Label {
            private String name;
            private String color;
        }
    }
    @Builder
    public static class Response {

    }
}
