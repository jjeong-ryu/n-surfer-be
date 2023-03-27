package com.notion.nsurfer.card.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class UpdateCardDto {
    @Getter
    @Builder
    public static class Request {
        private String title;
        private String content;
        private List<Label> labels;
        @Getter
        @Builder
        public static class Label {
            private String name;
            private String color;
        }
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("id")
        private String cardId;

    }
}
