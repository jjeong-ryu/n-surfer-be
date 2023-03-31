package com.notion.nsurfer.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetCardsDto {

    @Getter
    @Builder
    public static class Response {
        private List<Card> cardList;
        @Getter
        @Builder
        public static class Card {
            private String cardId;
            private String nickname;
            private String title;
            private String content;
            private LocalDateTime createDate;
            private LocalDateTime lastEditDate;
            private List<Label> labels;
            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
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
}
