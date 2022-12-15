package com.notion.nsuffer.card.service;

import com.notion.nsuffer.card.dto.NotionGetSyncDbDto;
import com.notion.nsuffer.card.repository.CardRepository;
import com.notion.nsuffer.common.ResponseDto;
import com.notion.nsuffer.mypage.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotionCardService {
    private final CardRepository cardRepository;
    private final String NOTION_URL = "https://api.notion.com/v1/databases/";
    private final String VERSION = "2022-06-28";
    public ResponseDto<Object> syncWithNotionDB(User user){
        String apiKey = user.getNotionApiKey();
        String dbId = user.getNotionDbId();

        WebClient webClient = WebClient.builder()
                .baseUrl(NOTION_URL + dbId)
                .defaultHeader("Notion-Version", "2022-06-28")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        Mono<NotionGetSyncDbDto.Response> result = webClient.get().retrieve().bodyToMono(NotionGetSyncDbDto.Response.class);
        result.block();

        // 해당 정보를 우리의 DB에 저장
        return ResponseDto.builder().build();
    }
}
