package com.notion.nsurfer.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class GetCardListDto {

    @Getter
    @Builder
    public static class Response {
        private List<Card> cardList;
        @Getter
        @Builder
        public static class Card {
            private String cardId;
            private String username;
            private String title;
            private List<Label> label;
            private String content;
            private LocalDateTime createDate;
            private Date lastEditDate;

            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Label {
                private String name;
                private String color;
            }
        }
    }
}
