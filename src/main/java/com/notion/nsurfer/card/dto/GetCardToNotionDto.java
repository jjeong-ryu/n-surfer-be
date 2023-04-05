package com.notion.nsurfer.card.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetCardToNotionDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String id;
        @JsonProperty("created_time")
        private LocalDateTime createdTime;
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
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Name {
                @Builder.Default
                private List<Title> title = new ArrayList<>();

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
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Label {
                @Builder.Default
                private String type = "multi_select";
                @JsonProperty("multi_select")
                @Builder.Default
                private List<MultiSelect> multiSelect = new ArrayList<>();

                @Getter
                @Builder
                @AllArgsConstructor
                @NoArgsConstructor
                public static class MultiSelect {
                    private String name;
                    private String color;
                }
            }

            @JsonProperty("Content")
            private Content content;

            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Content {
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
                @Builder.Default
                private List<GetCardsToNotionWithPagingDto.Response.Result.Properties.Creator.RichText> richTexts = new ArrayList<>();

                @Getter
                @Builder
                @AllArgsConstructor
                @NoArgsConstructor
                public static class RichText {
                    @Builder.Default
                    private String type = "text";
                    private GetCardsToNotionWithPagingDto.Response.Result.Properties.Creator.RichText.Text text;

                    @Getter
                    @Builder
                    @AllArgsConstructor
                    @NoArgsConstructor
                    public static class Text {
                        private String content;
                        private GetCardsToNotionWithPagingDto.Response.Result.Properties.Creator.RichText.Text.Link link;

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

            private File file;

            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class File {
                @Builder.Default
                private String type = "files";
                private List<ImageFile> files = new ArrayList<>();

                @Getter
                @Builder
                @AllArgsConstructor
                @NoArgsConstructor
                public static class ImageFile {
                    private String name;
                    @Builder.Default
                    private String type = "external";
                    private External external;

                    @Getter
                    @Builder
                    @AllArgsConstructor
                    @NoArgsConstructor
                    public static class External {
                        private String url;
                    }
                }
            }
        }
    }
}
