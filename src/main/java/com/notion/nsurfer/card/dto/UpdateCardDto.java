package com.notion.nsurfer.card.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class UpdateCardDto {
    @Getter
    public static class Request {
        private String name;
        private String content;
        private List<PostCardDto.Request.Label> label;
        public static class Label {
            private String name;
            private String color;
        }
    }
    @Getter
    @Builder
    public static class Response {
        @JsonProperty("id")
        private String cardId;

    }
}
