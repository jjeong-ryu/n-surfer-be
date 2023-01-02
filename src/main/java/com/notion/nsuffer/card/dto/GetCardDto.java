package com.notion.nsuffer.card.dto;

import com.notion.nsuffer.card.entity.Label;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

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
    }
}
