package com.notion.nsurfer.card.dto;

import lombok.Builder;
import lombok.Getter;

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
            private Long cardId;
            private Long userId;
            private String title;
            private String label;
            private String content;
            private Date createDate;
            private Date lastEditDate;
        }
    }
}
