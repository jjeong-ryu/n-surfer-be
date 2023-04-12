package com.notion.nsurfer.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.notion.nsurfer.card.dto.PostCardToNotionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UpdateCardNicknameDto {
    @Getter
    @Builder
    public static class Request {
        private Parent parent;
        private Properties properties;

        @Getter
        @Builder
        public static class Parent {
            @JsonProperty("database_id")
            private String databaseId;
        }

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Properties {
            @JsonProperty("Creator")
            private Creator creator;

            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Creator {
                @Builder.Default
                private String type = "rich_text";
                @JsonProperty("rich_text")
                private List<RichText> richTexts;

                @Getter
                @Builder
                @AllArgsConstructor
                @NoArgsConstructor
                public static class RichText {
                    @Builder.Default
                    private String type = "text";
                    private Text text;

                    @Getter
                    @Builder
                    @AllArgsConstructor
                    @NoArgsConstructor
                    public static class Text {
                        private String content;

                    }
                }

            }
        }
    }
}
