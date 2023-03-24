package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.CardImage;
import com.notion.nsurfer.card.entity.QCardImage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.notion.nsurfer.card.entity.QCardImage.*;

@Repository
@RequiredArgsConstructor
public class CardImageRepositoryCustomImpl implements CardImageRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public List<CardImage> findByCardId(UUID cardId){
        return queryFactory
                .selectFrom(cardImage)
                .where(cardIdEq(cardId))
                .fetch();
    }

    private BooleanExpression cardIdEq(UUID cardId){
        return cardId != null ? cardImage.card.id.eq(cardId) : null;
    }
}
