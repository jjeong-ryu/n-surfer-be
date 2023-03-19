package com.notion.nsurfer.card.dto;


import lombok.Builder;
import lombok.Getter;

public class UpdateCardToNotionDto {

    @Getter
    @Builder
    public static class Request {
        @Builder.Default
        private Boolean archived = true;
    }

    @Getter
    @Builder
    public static class Response {
        private String cardId;
    }
}
