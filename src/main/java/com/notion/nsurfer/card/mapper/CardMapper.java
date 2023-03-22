package com.notion.nsurfer.card.mapper;

import com.notion.nsurfer.card.dto.*;
import com.notion.nsurfer.common.CommonMapperConfig;
import com.notion.nsurfer.user.entity.User;
import org.cloudinary.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = CommonMapperConfig.class)
public interface CardMapper {
    default GetCardsToNotionDto.Request getCardsToNotionRequest(final String username){
        List<GetCardsToNotionDto.Request.And> ands = new ArrayList<>();
        ands.add(GetCardsToNotionDto.Request.And.builder()
                .property("Creator")
                .contains(username).build());
        return GetCardsToNotionDto.Request.builder()
                .ands(ands)
                .build();
    }

    default GetCardListDto.Response getCardsToResponse(GetCardsToNotionDto.Response response){
        return GetCardListDto.Response.builder()
                .cardList(getCardsToCardList(response.getResults()))
                .build();
    }

    default List<GetCardListDto.Response.Card> getCardsToCardList(List<GetCardsToNotionDto.Response.Result> results){
        List<GetCardListDto.Response.Card> cardList = new ArrayList<>();

        for (GetCardsToNotionDto.Response.Result result : results) {
            GetCardListDto.Response.Card card = GetCardListDto.Response.Card.builder()
                                    .cardId(result.getId())
                                    .username(result.getProperties().getCreator().getRichTexts().get(0).getText().getContent())
                                    .content(result.getProperties().getContent().getRichTexts().get(0).getText().getContent())
                                    .createDate(result.getCreatedTime())
                                    .title(result.getProperties().getName().getTitle().get(0).getText().getContent())
                                    .label(getCardsToCardLabels(result.getProperties().getLabel().getMultiSelect()))
                                    .build();

            cardList.add(card);
        }
        return cardList;
    }

    List<GetCardListDto.Response.Card.Label> getCardsToCardLabels(List<GetCardsToNotionDto.Response.Result.Properties.Label.MultiSelect> multiSelect);
    GetCardListDto.Response.Card.Label getCardsToCardLabel(GetCardsToNotionDto.Response.Result.Properties.Label.MultiSelect label);

    default PostCardToNotionDto.Request postCardToRequest(
            PostCardDto.Request dto,
            Long userId,
            String dbId,
            List<String> imageUrls,
            List<String> imageNames
    ){
        List<PostCardToNotionDto.Request.Properties.Name.Title> titles = new ArrayList<>();
        List<PostCardToNotionDto.Request.Properties.Content.RichText> contentRichTexts = new ArrayList<>();
        List<PostCardToNotionDto.Request.Properties.Creator.RichText> creatorRichTexts = new ArrayList<>();
        List<PostCardToNotionDto.Request.Properties.Label.MultiSelect> multiSelects;
        List<PostCardToNotionDto.Request.Properties.File.ImageFile> dtoFiles
                = postCardToFileImageFiles(imageUrls, imageNames);

        PostCardToNotionDto.Request.Properties.Content.RichText contentRichText = PostCardToNotionDto.Request.Properties.Content.RichText.builder()
                .text(PostCardToNotionDto.Request.Properties.Content.RichText.Text.builder()
                        .content(dto.getContent())
                        .build())
                .build();
        PostCardToNotionDto.Request.Properties.Name.Title title = PostCardToNotionDto.Request.Properties.Name.Title.builder()
                .text(PostCardToNotionDto.Request.Properties.Name.Title.Text.builder()
                        .content(dto.getName()).build())
                .build();
        PostCardToNotionDto.Request.Properties.Creator.RichText creatorRichText = PostCardToNotionDto.Request.Properties.Creator.RichText.builder()
                .text(
                        PostCardToNotionDto.Request.Properties.Creator.RichText.Text.builder()
                                .content(String.valueOf(userId))
                                .build()
                )
                .build();

        titles.add(title);
        contentRichTexts.add(contentRichText);
        creatorRichTexts.add(creatorRichText);
        multiSelects = postCardToLabelMultiSelects(dto.getLabels());
        return PostCardToNotionDto.Request.builder()
                .parent(
                    PostCardToNotionDto.Request.Parent.builder()
                            .databaseId(dbId).build()
                )
                .properties(
                    PostCardToNotionDto.Request.Properties.builder()
                        .name(
                            PostCardToNotionDto.Request.Properties.Name.builder()
                                .title(
                                        titles
                                ).build()
                        )
                        .content(
                            PostCardToNotionDto.Request.Properties.Content.builder()
                                .richTexts(
                                        contentRichTexts
                                )
                                .build()
                        )
                        .label(
                            PostCardToNotionDto.Request.Properties.Label.builder()
                                .multiSelect(multiSelects)
                                .build()
                        )
                        .creator(
                            PostCardToNotionDto.Request.Properties.Creator.builder()
                                .richTexts(creatorRichTexts)
                                .build()
                        )
                        .file(
                                PostCardToNotionDto.Request.Properties.File.builder()
                                        .files(dtoFiles)
                                        .build()
                        )
                        .build())
                .build();
    }
    List<PostCardToNotionDto.Request.Properties.Label.MultiSelect> postCardToLabelMultiSelects(
            List<PostCardDto.Request.Label> labels
    );

    PostCardToNotionDto.Request.Properties.Label.MultiSelect postCardToLabelMultiSelect(
            PostCardDto.Request.Label label
    );

    default List<PostCardToNotionDto.Request.Properties.File.ImageFile> postCardToFileImageFiles(
            List<String> imageUrls, List<String> imageNames
    ){
        List<PostCardToNotionDto.Request.Properties.File.ImageFile> imageFiles = new ArrayList<>();
        for (int idx = 0; idx < imageUrls.size(); idx++) {
            PostCardToNotionDto.Request.Properties.File.ImageFile imageFile =
                    PostCardToNotionDto.Request.Properties.File.ImageFile.builder()
                            .name(imageNames.get(idx))
                            .external(PostCardToNotionDto.Request.Properties.File.ImageFile.External.builder()
                                    .url(imageUrls.get(idx))
                                    .build())
                            .build();
            imageFiles.add(imageFile);
        }
        return imageFiles;
    }

}
