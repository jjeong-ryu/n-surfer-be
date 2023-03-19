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
                private PostCardToNotionDto.Request.Properties.Name name;

                @Getter
                @Builder
                public static class Name {
                    private List<PostCardToNotionDto.Request.Properties.Name.Title> title;

                    @Getter
                    @Builder
                    @AllArgsConstructor
                    @NoArgsConstructor
                    public static class Title {
                        private PostCardToNotionDto.Request.Properties.Name.Title.Text text;

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
                private PostCardToNotionDto.Request.Properties.Label label;

                @Getter
                @Builder
                public static class Label {
                    @Builder.Default
                    private String type = "multi_select";
                    @JsonProperty("multi_select")
                    private List<PostCardToNotionDto.Request.Properties.Label.MultiSelect> multiSelect;

                    @Getter
                    @Builder
                    public static class MultiSelect {
                        private String name;
                        private String color;
                    }
                }

                @JsonProperty("Content")
                private PostCardToNotionDto.Request.Properties.Content content;

                @Getter
                @Builder
                public static class Content {
                    @Builder.Default
                    private String type = "rich_text";
                    @JsonProperty("rich_text")
                    private List<PostCardToNotionDto.Request.Properties.Content.RichText> richTexts;

                    @Getter
                    @Builder
                    public static class RichText {
                        @Builder.Default
                        private String type = "text";
                        private PostCardToNotionDto.Request.Properties.Content.RichText.Text text;

                        @Getter
                        @Builder
                        public static class Text {
                            private String content;
                            private PostCardToNotionDto.Request.Properties.Content.RichText.Text.Link link;

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
                private PostCardToNotionDto.Request.Properties.Creator creator;

                @Getter
                @Builder
                @AllArgsConstructor
                @NoArgsConstructor
                public static class Creator {
                    @Builder.Default
                    private String type = "rich_text";
                    @JsonProperty("rich_text")
                    private List<PostCardToNotionDto.Request.Properties.Creator.RichText> richTexts;

                    @Getter
                    @Builder
                    public static class RichText {
                        @Builder.Default
                        private String type = "text";
                        private PostCardToNotionDto.Request.Properties.Creator.RichText.Text text;

                        @Getter
                        @Builder

                        public static class Text {
                            private String content;
                            private PostCardToNotionDto.Request.Properties.Creator.RichText.Text.Link link;

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
