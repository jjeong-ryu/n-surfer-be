package com.notion.nsurfer.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotionUtils {
    public static String apiKey;
    public static String dbId;
    public static final String NOTION_CARD_URL = "https://api.notion.com/v1/pages/";
    public static final String NOTION_DB_URL = "https://api.notion.com/v1/databases/";
    public static final String VERSION = "2022-06-28";

    @Value("${notion.token}")
    public void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }

    @Value("${notion.dbId}")
    public void setDbId(String dbId){
        this.dbId = dbId;
    }
}
