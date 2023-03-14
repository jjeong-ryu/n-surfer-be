package com.notion.nsurfer.card.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetCardDto {
    @Getter
    @Builder
    public static class Response {
        private Long cardId;
        private Long userId;
        private String title;
        private String content;
        private Date createDate;
        private Date lastEditDate;
        private Label label;
        @Getter
        @Builder
        public static class Label {
            private String name;
            private String color;
        }
        @Builder.Default
        private List<Image> images = new ArrayList<>();

        @Getter
        @Builder
        public static class Image {
            private Long imageId;
            private String imageUrl;
        }
    }
}
