package com.notion.nsuffer.card.dto;

import lombok.Builder;
import lombok.Getter;

public class PostCardDto {

    @Getter
    public static class Request {
        private String name;
        private String content;
        private Label label;
        private String LastEditedDate;
        private String createdDate;
        private String pageCreator;
        private String DBCreator;
        public static class Label {
            private String name;
            private String color;
        }
    }
    @Builder
    public static class Response {

    }
}
