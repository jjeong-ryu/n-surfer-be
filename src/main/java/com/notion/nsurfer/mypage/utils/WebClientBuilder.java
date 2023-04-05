package com.notion.nsurfer.mypage.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientBuilder {
    public static final String NOTION_CARD_URL = "https://api.notion.com/v1/pages/";
    @Value("${notion.token}")
    public static String apiKey = "secret_bGUWaYI7PCpufgCmO7vX4pFD3Qt1kT2qiCv1aAJhcxR";
    @Value("${notion.dbId}")
    private String dbId;
    public static final String VERSION = "2022-06-28";
    public static WebClient cardWebclientBuilder(String url){
        return WebClient.builder()
                .baseUrl(NOTION_CARD_URL + url)
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
