package com.notion.nsurfer.card.mapper;

import com.notion.nsurfer.card.dto.PostCardDto;
import com.notion.nsurfer.card.dto.PostCardToNotionDto;
import com.notion.nsurfer.common.CommonMapperConfig;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = CommonMapperConfig.class)
public interface CardMapper {
    default PostCardToNotionDto.Request postCardToRequest(
            PostCardDto.Request dto,
            Long userId,
            String dbId
    ){
        List<PostCardToNotionDto.Request.Properties.Name.Title> titles = new ArrayList<>();
        List<PostCardToNotionDto.Request.Properties.Content.RichText> contentRichTexts = new ArrayList<>();
        List<PostCardToNotionDto.Request.Properties.Creator.RichText> creatorRichTexts = new ArrayList<>();
        List<PostCardToNotionDto.Request.Properties.Label.MultiSelect> multiSelects;

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
        multiSelects = postCardToLabelMultiSelects(dto.getLabel());
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
                        .build())
                .build();
    }
    List<PostCardToNotionDto.Request.Properties.Label.MultiSelect> postCardToLabelMultiSelects(
            List<PostCardDto.Request.Label> labels
    );

    PostCardToNotionDto.Request.Properties.Label.MultiSelect postCardToLabelMultiSelect(
            PostCardDto.Request.Label label
    );

}
