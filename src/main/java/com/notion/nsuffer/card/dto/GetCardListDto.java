package com.notion.nsuffer.card.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

public class GetCardListDto {

    @Getter
    @Builder
    public static class Response {
        private List<Card> cardList;
        public static class Card {
            private Long cardId;
            private Long userId;
            private String title;
            private String label;
            private String content;
            private Date createDate;
            private Date lastEditDate;
            private Date prevEditDate;
        }
    }
}
