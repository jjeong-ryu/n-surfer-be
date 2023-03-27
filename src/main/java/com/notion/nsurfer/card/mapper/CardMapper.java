package com.notion.nsurfer.card.mapper;

import com.notion.nsurfer.card.dto.*;
import com.notion.nsurfer.common.CommonMapperConfig;
import com.notion.nsurfer.user.entity.User;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = CommonMapperConfig.class)
public interface CardMapper {
    default GetCardsToNotionDto.Request getCardsToNotionRequest(final String username){
        return GetCardsToNotionDto.Request.builder()
                .filter(GetCardsToNotionDto.Request.Filter.builder()
                        .property("Creator")
                        .richText(GetCardsToNotionDto.Request.Filter.RichText.builder()
                                .contains(username)
                                .build())
                        .build())
                .build();
    }

    default GetCardsDto.Response getCardsToResponse(GetCardsToNotionDto.Response response){
        return GetCardsDto.Response.builder()
                .cardList(getCardsToCardList(response.getResults()))
                .build();
    }

    default List<GetCardsDto.Response.Card> getCardsToCardList(List<GetCardsToNotionDto.Response.Result> results){
        List<GetCardsDto.Response.Card> cardList = new ArrayList<>();

        for (GetCardsToNotionDto.Response.Result result : results) {
            String username = result.getProperties().getCreator().getRichTexts().size() > 0
                    ? result.getProperties().getCreator().getRichTexts().get(0).getText().getContent()
                    : "";
            String content = result.getProperties().getContent().getRichTexts().size() > 0
                    ? result.getProperties().getContent().getRichTexts().get(0).getText().getContent()
                    : "";
            String title = result.getProperties().getName().getTitle().size() > 0
                    ? result.getProperties().getName().getTitle().get(0).getText().getContent()
                    : "";
            List<GetCardsDto.Response.Card.Image> images =
                    result.getProperties().getFile().getFiles().size() > 0
                    ? getCardsToCardImages(result.getProperties().getFile().getFiles())
                    : new ArrayList<>();
            List<GetCardsDto.Response.Card.Label> labels =
                    result.getProperties().getLabel().getMultiSelect().size() > 0
                    ? getCardsToCardLabels(result.getProperties().getLabel().getMultiSelect())
                    : new ArrayList<>();
            GetCardsDto.Response.Card card = GetCardsDto.Response.Card.builder()
                                    .cardId(result.getId())
                                    .username(username)
                                    .content(content)
                                    .createDate(result.getCreatedTime())
                                    .title(title)
                                    .labels(labels)
                                    .images(images)
                                    .build();

            cardList.add(card);
        }
        return cardList;
    }
    List<GetCardsDto.Response.Card.Image> getCardsToCardImages(List<GetCardsToNotionDto.Response.Result.Properties.File.ImageFile> images);

    default GetCardsDto.Response.Card.Image getCardsToCardImage(GetCardsToNotionDto.Response.Result.Properties.File.ImageFile image){
        return GetCardsDto.Response.Card.Image.builder()
                .imageId(String.valueOf(image.getName()))
                .imageUrl(image.getExternal().getUrl())
                .build();
    }
    List<GetCardsDto.Response.Card.Label> getCardsToCardLabels(List<GetCardsToNotionDto.Response.Result.Properties.Label.MultiSelect> multiSelect);
    GetCardsDto.Response.Card.Label getCardsToCardLabel(GetCardsToNotionDto.Response.Result.Properties.Label.MultiSelect label);

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
                        .content(dto.getTitle()).build())
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
    default UpdateCardToNotionDto.Request updateCardToNotionRequest(
            UpdateCardDto.Request dto,
            List<MultipartFile> files,
            User user
    ){
        UpdateCardToNotionDto.Request.Properties properties = UpdateCardToNotionDto.Request.Properties.builder()
                .name(updateCardToNotionRequestName(dto.getTitle()))
                .label(updateCardToNotionRequestLabel(dto.getLabels()))
                .file(updateCardToNotionRequestFile(files))
                .creator(updateCardToNotionRequestCreator(user))
                .content(updateCardToNotionRequestContent(dto.getContent())).build();
        return UpdateCardToNotionDto.Request.builder()
                .properties(properties)
                .build();
    }

    default UpdateCardToNotionDto.Request.Properties.Content updateCardToNotionRequestContent(String content){
        List<UpdateCardToNotionDto.Request.Properties.Content.RichText> richTexts = new ArrayList<>();
        UpdateCardToNotionDto.Request.Properties.Content.RichText richText = UpdateCardToNotionDto.Request.Properties.Content.RichText.builder()
                .text(
                        UpdateCardToNotionDto.Request.Properties.Content.RichText.Text.builder()
                                .content(content).build()
                ).build();
        richTexts.add(richText);
        return UpdateCardToNotionDto.Request.Properties.Content.builder()
                .richTexts(richTexts)
                .build();
    }

    default UpdateCardToNotionDto.Request.Properties.Label updateCardToNotionRequestLabel(
            List<UpdateCardDto.Request.Label> labels){
        List<UpdateCardToNotionDto.Request.Properties.Label.MultiSelect> multiSelects = new ArrayList<>();
        for (UpdateCardDto.Request.Label label : labels) {
            UpdateCardToNotionDto.Request.Properties.Label.MultiSelect multiSelect = UpdateCardToNotionDto.Request.Properties.Label.MultiSelect.builder()
                    .color(label.getColor())
                    .name(label.getName()).build();
            multiSelects.add(multiSelect);
        }
        return UpdateCardToNotionDto.Request.Properties.Label.builder()
                .multiSelect(multiSelects).build();
    }
    default UpdateCardToNotionDto.Request.Properties.File updateCardToNotionRequestFile(List<MultipartFile> files){
        return null;
    }
    default UpdateCardToNotionDto.Request.Properties.Creator updateCardToNotionRequestCreator(User user){
        List<UpdateCardToNotionDto.Request.Properties.Creator.RichText> richTexts = new ArrayList<>();
        UpdateCardToNotionDto.Request.Properties.Creator.RichText richText = UpdateCardToNotionDto.Request.Properties.Creator.RichText.builder()
                .text(UpdateCardToNotionDto.Request.Properties.Creator.RichText.Text.builder()
                        .content(user.getUsername()).build())
                .build();
        richTexts.add(richText);
        return UpdateCardToNotionDto.Request.Properties.Creator.builder()
                .richTexts(richTexts).build();
    }
    default UpdateCardToNotionDto.Request.Properties.Name updateCardToNotionRequestName(String title){
        List<UpdateCardToNotionDto.Request.Properties.Name.Title> titles = new ArrayList<>();
        titles.add(UpdateCardToNotionDto.Request.Properties.Name.Title.builder()
                .text(
                        UpdateCardToNotionDto.Request.Properties.Name.Title.Text.builder()
                                .content(title).build()
                ).build());
        return UpdateCardToNotionDto.Request.Properties.Name
                .builder()
                .title(titles).build();
    }

    default GetCardDto.Response getCardToResponse(GetCardToNotionDto.Response result){
        String username = result.getProperties().getCreator().getRichTexts().size() > 0
                ? result.getProperties().getCreator().getRichTexts().get(0).getText().getContent()
                : "";
        String content = result.getProperties().getContent().getRichTexts().size() > 0
                ? result.getProperties().getContent().getRichTexts().get(0).getText().getContent()
                : "";
        String title = result.getProperties().getName().getTitle().size() > 0
                ? result.getProperties().getName().getTitle().get(0).getText().getContent()
                : "";
        List<GetCardDto.Response.Image> images =
                result.getProperties().getFile().getFiles().size() > 0
                        ? getCardToCardImages(result.getProperties().getFile().getFiles())
                        : new ArrayList<>();
        List<GetCardDto.Response.Label> labels =
                result.getProperties().getLabel().getMultiSelect().size() > 0
                        ? getCardToCardLabels(result.getProperties().getLabel().getMultiSelect())
                        : new ArrayList<>();
        return GetCardDto.Response.builder()
                .cardId(result.getId())
                .username(username)
                .content(content)
                .createDate(result.getCreatedTime())
                .title(title)
                .labels(labels)
                .images(images)
                .build();
    }
    List<GetCardDto.Response.Image> getCardToCardImages(List<GetCardToNotionDto.Response.Properties.File.ImageFile> images);

    default GetCardDto.Response.Image getCardToCardImage(GetCardToNotionDto.Response.Properties.File.ImageFile image){
        return GetCardDto.Response.Image.builder()
                .imageId(image.getName())
                .imageUrl(image.getExternal().getUrl())
                .build();
    }
    List<GetCardDto.Response.Label> getCardToCardLabels(List<GetCardToNotionDto.Response.Properties.Label.MultiSelect> multiSelect);
    GetCardDto.Response.Label getCardToCardLabel(GetCardToNotionDto.Response.Properties.Label.MultiSelect label);
}
