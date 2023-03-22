package com.notion.nsurfer.card.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GetCardDto {
    @Getter
    @Builder
    public static class Response {
        private String cardId;
        private String username;
        private String title;
        private String content;
        private LocalDateTime createDate;
        private LocalDateTime lastEditDate;
        private List<Label> labels;
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
            private String imageId;
            private String imageUrl;
        }
    }
}
