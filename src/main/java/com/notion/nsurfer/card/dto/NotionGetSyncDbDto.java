package com.notion.nsurfer.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class NotionGetSyncDbDto {
    @Getter
    public static class Response {
        List<Result> results = new ArrayList<>();
        @Getter
        public static class Result {
            private String cover;
            private Properties properties;
            @Getter
            public static class Properties {
                @JsonProperty("Label")
                private Label label;
                @Getter
                public static class Label {
                    private String type;
                }
            }
        }
    }
}
