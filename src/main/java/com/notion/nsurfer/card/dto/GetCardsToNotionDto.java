package com.notion.nsurfer.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class GetCardsToNotionDto {
    @Getter
    @Builder
    public static class Request {
        @JsonProperty("and")
        private List<And> ands;

        @Getter
        @Builder
        public static class And {
            private String property;
            private String contains;
        }
    }
    @Getter
    @Builder
    public static class Response {
        private List<Result> results;

        @Getter
        @Builder
        public static class Result {
            private String id;
            @JsonProperty("created_time")
            private LocalDateTime createdTime;
            @JsonProperty("last_edited_time")
            private LocalDateTime lastEditedTime;

            private Properties properties;

            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Properties {
                @JsonProperty("Name")
                private Name name;

                @Getter
                @Builder
                public static class Name {
                    private List<Title> title;

                    @Getter
                    @Builder
                    @AllArgsConstructor
                    @NoArgsConstructor
                    public static class Title {
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

                @JsonProperty("Label")
                private Label label;

                @Getter
                @Builder
                public static class Label {
                    @Builder.Default
                    private String type = "multi_select";
                    @JsonProperty("multi_select")
                    private List<MultiSelect> multiSelect;

                    @Getter
                    @Builder
                    public static class MultiSelect {
                        private String name;
                        private String color;
                    }
                }

                @JsonProperty("Content")
                private Content content;

                @Getter
                @Builder
                public static class Content {
                    @Builder.Default
                    private String type = "rich_text";
                    @JsonProperty("rich_text")
                    private List<RichText> richTexts;

                    @Getter
                    @Builder
                    public static class RichText {
                        @Builder.Default
                        private String type = "text";
                        private Text text;

                        @Getter
                        @Builder
                        public static class Text {
                            private String content;
                            private Link link;

                            @Getter
                            @Builder
                            @AllArgsConstructor
                            @NoArgsConstructor

                            public static class Link {
                                private String url;
                            }
                        }
                    }
                }

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
                    public static class RichText {
                        @Builder.Default
                        private String type = "text";
                        private Text text;

                        @Getter
                        @Builder

                        public static class Text {
                            private String content;
                            private Link link;

                            @Getter
                            @Builder
                            @AllArgsConstructor
                            @NoArgsConstructor
                            public static class Link {
                                private String url;
                            }
                        }
                    }
                }
            }
        }
    }
}
