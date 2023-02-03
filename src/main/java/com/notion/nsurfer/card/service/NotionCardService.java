//package com.notion.nsurfer.card.service;
//
//import com.notion.nsurfer.card.dto.NotionGetSyncDbDto;
//import com.notion.nsurfer.card.repository.CardRepository;
//import com.notion.nsurfer.common.ResponseCode;
//import com.notion.nsurfer.common.ResponseDto;
//import com.notion.nsurfer.user.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Service
//@RequiredArgsConstructor
//public class NotionCardService {
//    private final CardRepository cardRepository;
//    private final String NOTION_URL = "https://api.notion.com/v1/databases/";
//    private final String VERSION = "2022-06-28";
//    public ResponseDto<Object> syncWithNotionDB(User user){
////        String apiKey = user.getNotionApiKey();
//        String apiKey = "secret_bGUWaYI7PCpufgCmO7vX4pFD3Qt1kT2qiCv1aAJhcxR";
////        String dbId = user.getNotionDbId();
//        String dbId = "05e61a0a-a980-4d4d-b5a4-4f73a197ea7d";
//        WebClient webClient = WebClient.builder()
//                .baseUrl(NOTION_URL + dbId + "/query")
//                .defaultHeader("Notion-Version", "2022-06-28")
//                .defaultHeader("Authorization", "Bearer " + apiKey)
//                .build();
//        NotionGetSyncDbDto.Response result = webClient.post()
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(NotionGetSyncDbDto.Response.class)
//                .block();
//        // 해당 정보를 우리의 DB에 저장
//        return ResponseDto.builder()
//                .responseCode(ResponseCode.DB_SYNC)
//                .data(null)
//                .build();
//    }
//}
