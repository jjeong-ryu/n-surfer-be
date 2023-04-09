package com.notion.nsurfer.card.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.notion.nsurfer.card.dto.*;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.entity.CardImage;
import com.notion.nsurfer.card.exception.CardNotFoundException;
import com.notion.nsurfer.card.mapper.CardMapper;
import com.notion.nsurfer.card.repository.CardImageRepository;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.card.util.CardRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.utils.MyPageRedisKeyUtils;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final CardImageRepository cardImageRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String NOTION_CARD_URL = "https://api.notion.com/v1/pages/";
    private final String NOTION_DB_URL = "https://api.notion.com/v1/databases/";
    private final CardMapper cardMapper;
    private final Cloudinary cloudinary;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Value("${notion.token}")
    private String apiKey;
    @Value("${notion.dbId}")
    private String dbId;
    private final String VERSION = "2022-06-28";
    public ResponseDto<GetCardDto.Response> getCard(final UUID cardId){
        Card card = cardRepository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);
        User user = card.getUser();
        WebClient webClient = cardWebclientBuilder(cardId.toString());
        GetCardToNotionDto.Response notionResponse = webClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GetCardToNotionDto.Response.class)
                .block();
        // 해당 정보를 받아 최종적으로 프론트로 전달
        return ResponseDto.<GetCardDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(cardMapper.getCardToResponse(notionResponse, user.getNickname()))
                .build();
    }

    public ResponseDto<GetCardsDto.Response> getCards(final String nickname, String numOfCards,
                                                      final String nextCardId, final String label) {
        // username이 null이어도 가능
        numOfCards = numOfCards.equals("") ? "15" : numOfCards;
        WebClient webClient = dbQueryWebclientBuilder();
        GetCardsToNotionDto.Response notionResponse =
                !nextCardId.equals("")
                ? getNotionResponseWithPaging(webClient, nickname, numOfCards, nextCardId, label)
                : getNotionResponseWithoutPaging(webClient, nickname, numOfCards, label);

        GetCardsDto.Response responseData = cardMapper.getCardsToResponse(notionResponse, numOfCards);

        // 현재 DB에 저장된 모든 카드 return
        return ResponseDto.<GetCardsDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(responseData)
                .build();
    }
    private GetCardsToNotionDto.Response getNotionResponseWithPaging(
            WebClient webClient, final String nickname, final String numOfCards, final String nextCardId, final String label
    ) {
        return webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.getCardsToNotionWithPagingRequest(nickname, Integer.valueOf(numOfCards), nextCardId, label))
                .retrieve()
                .bodyToMono(GetCardsToNotionDto.Response.class)
                .block();
    }
    private GetCardsToNotionDto.Response getNotionResponseWithoutPaging(
            WebClient webClient, final String nickname, final String numOfCards, final String label
    ) {
        return webClient.post()
                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue(cardMapper.getCardsToNotionWithoutPagingRequest(nickname, Integer.valueOf(numOfCards), label))
                .bodyValue(BodyInserters.fromValue(makeGetCardsBody(nickname, Integer.valueOf(numOfCards), label, "")))
                .retrieve()
                .bodyToMono(GetCardsToNotionDto.Response.class)
                .block();
    }

    @Transactional
    public ResponseDto<PostCardDto.Response> postCard(PostCardDto.Request dto, List<MultipartFile> imgFiles, User user) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        List<String> imageNames = new ArrayList<>();
        // 이미지 업로드 및 이미지 url 저장
        if(imgFiles != null){
            for (int idx = 0; idx < imgFiles.size(); idx++) {
                MultipartFile image = imgFiles.get(idx);
                uploadImageToCloudinary(user, imageUrls, imageNames, image);
            }
        }
        // card(page)를 노션에 저장하고, 해당 id를 db에 저장
        PostCardToNotionDto.Response notionResponse = postCardToNotion(dto, user, imageUrls, imageNames);

        Card card = cardRepository.save(Card.builder()
                .id(UUID.fromString(notionResponse.getCardId()))
                .user(user).build());

        for (int idx = 0; idx < imageUrls.size(); idx++) {
            saveCardImage(imageUrls, imageNames, card, idx);
        }

        // wave에 오늘 날짜 잔디 수 value + 1, total +1,  cardId에 생성이력 추가
        String waveKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        addWaveToToday(waveKey);
        addWaveToTotal(waveKey);
        String cardKey = CardRedisKeyUtils.makeRedisCardHistoryValue(UUID.fromString(notionResponse.getCardId()));
        addCardHistory(cardKey);

        return ResponseDto.<PostCardDto.Response>builder()
                .responseCode(ResponseCode.POST_CARD)
                .data(PostCardDto.Response.builder()
                        .cardId(card.getId()).build())
                .build();
    }

    private void saveCardImage(List<String> imageUrls, List<String> imageNames, Card card, int idx) {
        CardImage cardImage = CardImage.builder()
                .id(UUID.randomUUID())
                .url(imageUrls.get(idx))
                .card(card)
                .cardImageName(imageNames.get(idx)).build();
        cardImageRepository.save(cardImage);
    }

    private PostCardToNotionDto.Response postCardToNotion(PostCardDto.Request dto, User user, List<String> imageUrls, List<String> imageNames) {
        WebClient webClient = cardWebclientBuilder("");

        PostCardToNotionDto.Response notionResponse = webClient.post()
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(cardMapper.postCardToRequest(dto, user.getNickname(), dbId, imageUrls, imageNames))
                    .retrieve()
                    .bodyToMono(PostCardToNotionDto.Response.class)
                    .block();
        return notionResponse;
    }

    private void uploadImageToCloudinary(User user, List<String> imageUrls, List<String> imageNames, MultipartFile image) throws IOException {
        String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider(), UUID.randomUUID().toString()), "_");
        Map uploadResponse = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("public_id", imageName));
        String url = uploadResponse.get("url").toString();
        imageUrls.add(url);
        imageNames.add(imageName);
    }

    private void addCardHistory(String cardKey) {
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        String historyValue = "create:" + LocalDate.now().toString().replace("-", "");
        opsForList.rightPush((cardKey), historyValue);
    }

    private void addWaveToToday(String waveKey) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWaveTimeFormat = LocalDate.now().toString().replace("-", "");


        String todayWave = opsForHash.get(waveKey,"total");
        Integer intTotalWave = todayWave != null ? Integer.valueOf(todayWave) + 1 : 1;
        opsForHash.put(waveKey, redisWaveTimeFormat, String.valueOf(intTotalWave));

    }
    private void addWaveToTotal(String waveKey) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String totalWave = opsForHash.get(waveKey,"total");
        Integer intTotalWave = totalWave != null ? Integer.valueOf(totalWave) + 1 : 1;
        opsForHash.put(waveKey, "total", String.valueOf(intTotalWave));
    }

    @Transactional
    public ResponseDto<Object> updateCard(UUID cardId, PostCardDto.Request dto, List<MultipartFile> addedImages, List<String> deletedImages,  User user) throws Exception {
        List<String> imageNames = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();
        List<GetCardDto.Response.Image> originalCardImages = this.getCard(cardId).getData().getImages();
        addNameAndUrlFromOriginalCardImages(imageNames, imageUrls, originalCardImages);

        Card card = cardRepository.findByIdWithImages(cardId)
                .orElseThrow(CardNotFoundException::new);
        List<CardImage> cardImages = card.getCardImages();

        // 이미지 제거 API
        if(deletedImages != null){
            deleteCardsFromNotionDBAndDB(deletedImages, cardImages, imageNames, imageUrls);
        }
        // 이미지 추가 API
        if(addedImages != null){
            addImagesToNotionDBAndDB(addedImages, user, imageNames, imageUrls, card, cardImages);
        }
        // notion 상에서의 카드 정보 수정
        updateCardFromNotionDB(cardId, dto, user, imageNames, imageUrls);

        // wave 추가
        addWave(cardId, user);

        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_CARD)
                .data(null).build();
    }

    private void addWave(UUID cardId, User user) {
        addCardWave(cardId);

        String waveKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        addWaveToToday(waveKey);
        addWaveToTotal(waveKey);
    }

    private void addCardWave(UUID cardId) {
        ListOperations<String, String> ops = redisTemplate.opsForList();
        String historyValue = "update:" + LocalDate.now().toString().replace("-", "");
        ops.rightPush(cardId.toString(), historyValue);
    }

    private UpdateCardToNotionDto.Response updateCardFromNotionDB(UUID cardId, PostCardDto.Request dto, User user, List<String> imageNames, List<String> imageUrls) {
        WebClient webClient = cardWebclientBuilder(cardId.toString());
        UpdateCardToNotionDto.Response result = webClient.patch()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.postCardToRequest(dto, user.getNickname(), dbId, imageUrls, imageNames))
                .retrieve()
                .bodyToMono(UpdateCardToNotionDto.Response.class)
                .block();
        return result;
    }

    private void addImagesToNotionDBAndDB(List<MultipartFile> addedImages, User user, List<String> imageNames, List<String> imageUrls, Card card, List<CardImage> cardImages) throws IOException {
        for (MultipartFile addedImage : addedImages) {
            // 카드 업로드 후 받은 url을 저장
            String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider(), UUID.randomUUID().toString()), "_");
            Map uploadResponse = cloudinary.uploader().upload(addedImage.getBytes(), ObjectUtils.asMap("public_id", imageName));
            String url = uploadResponse.get("url").toString();
            imageUrls.add(url);
            imageNames.add(imageName);
            CardImage cardImage = CardImage.builder()
                    .url(url)
                    .cardImageName(imageName)
                    .card(card)
                    .build();
            cardImages.add(cardImage);
        }
    }

    private void addNameAndUrlFromOriginalCardImages(List<String> imageNames, List<String> imageUrls, List<GetCardDto.Response.Image> originalCardImages) {
        for (GetCardDto.Response.Image originalCardImage : originalCardImages) {
            imageUrls.add(originalCardImage.getImageUrl());
            imageNames.add(originalCardImage.getImageId());
        }
    }

    private void deleteCardsFromNotionDBAndDB(List<String> deletedImages, List<CardImage> cardImages,
                                              List<String> imageNames, List<String> imageUrls) throws Exception {
        cloudinary.api().deleteResources(deletedImages, null);
        for (String deletedImage : deletedImages) {
            CardImage cardImage = cardImages.stream().filter(ci -> ci.getCardImageName().equals(deletedImage))
                    .findFirst()
                    .orElseThrow(CardNotFoundException::new);
            imageNames = deleteStringElementFromArray(imageNames, cardImage.getCardImageName());
            imageUrls = deleteStringElementFromArray(imageUrls, cardImage.getUrl());
            cardImages.remove(cardImage);
        }
    }
    private List<String> deleteStringElementFromArray(List<String> array, String element) {
        return array.stream().filter(el -> !el.equals(element)).toList();
    }

    @Transactional
    public ResponseDto<Object> deleteCard(UUID cardId, User user) throws Exception {
        // 카드 제거 요청 API
        deleteCardFromNotionDB(cardId);
        // 관련 이미지 db에서 제거 후 cloudinary에서도 제거
        Card deletedCard = cardRepository.findByIdWithImages(cardId)
                .orElseThrow(CardNotFoundException::new);
        if(deletedCard.getCardImages().size() > 0){
            deleteCardImagesFromNotionDB(deletedCard);
        }
        cardRepository.delete(deletedCard);
        // cardId 순회하면서 해당 일자에 해당하는 wave:email:provider hash값 -1
        deleteUserWave(cardId, user);
        return ResponseDto.builder()
                .responseCode(ResponseCode.DELETE_CARD)
                .data(null).build();
    }

    private void deleteUserWave(UUID cardId, User user) {
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        String cardRecordKey = CardRedisKeyUtils.makeRedisCardHistoryValue(cardId);
        String cardRecordValue = opsForList.leftPop(cardRecordKey);

        while(cardRecordValue != null){
            eraseWaveFromUser(cardRecordValue, user);
            cardRecordValue = opsForList.leftPop(cardRecordKey);
        }
    }

    private void deleteCardImagesFromNotionDB(Card deletedCard) throws Exception {
        List<String> deletedImages = deletedCard.getCardImages().stream().map(ci -> ci.getCardImageName()).collect(Collectors.toList());
        cloudinary.api().deleteResources(deletedImages, null);
    }

    private void deleteCardFromNotionDB(UUID cardId) {
        WebClient webClient = cardWebclientBuilder(cardId.toString());
        webClient.patch()
                .bodyValue(DeleteCardToNotionDto.Request.builder()
                        .archived(true).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DeleteCardDto.Response.class)
                .block();
    }

    private void eraseWaveFromUser(String cardRecordValue, User user) {
        String userWaveKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String userWaveHashKey = cardRecordValue.split(":")[1];
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String wave = opsForHash.get(userWaveKey, userWaveHashKey);
        opsForHash.put(userWaveKey, userWaveHashKey, String.valueOf(Integer.valueOf(wave) - 1));
        String totalWave = opsForHash.get(userWaveKey, "total");
        opsForHash.put(userWaveKey, "total", String.valueOf(Integer.valueOf(totalWave) - 1) );
    }

    private WebClient cardWebclientBuilder(String url){
        return WebClient.builder()
                .baseUrl(NOTION_CARD_URL + url)
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    private WebClient dbQueryWebclientBuilder(){
        return WebClient.builder()
                .baseUrl(NOTION_DB_URL + dbId + "/query")
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    private JsonObject makeGetCardsBody(final String nickname, final int pageSize,  final String label, final String nextCardId){
        JsonObject body = new JsonObject();
        JsonObject filterObject = new JsonObject();
        JsonArray andObject = new JsonArray();

        JsonObject creatorObject = new JsonObject();
        creatorObject.addProperty("property", "Creator");
        JsonObject richTextContains = new JsonObject();
        creatorObject.add("rich_text", richTextContains);
        richTextContains.addProperty("contains", nickname);

        andObject.add(creatorObject);

        JsonObject labelObject = new JsonObject();
        labelObject.addProperty("property", "Label");
        JsonObject multiSelectObject = new JsonObject();
        labelObject.add("multi_select", multiSelectObject);
        multiSelectObject.addProperty("contains", label);
        andObject.add(labelObject);

        filterObject.add("and", andObject);
        body.add("filter", filterObject);
        body.addProperty("page_size", pageSize);

        if(!nextCardId.equals("")){
            body.addProperty("start_cursor", nextCardId);
        }
        return body;
    }
}
