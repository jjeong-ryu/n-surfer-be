package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.entity.QCard;
import com.notion.nsurfer.card.entity.QCardImage;
import com.notion.nsurfer.user.entity.QUser;
import com.notion.nsurfer.user.entity.QWave;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.notion.nsurfer.card.entity.QCard.*;
import static com.notion.nsurfer.card.entity.QCardImage.*;
import static com.notion.nsurfer.user.entity.QUser.user;
import static com.notion.nsurfer.user.entity.QWave.*;

@Repository
@RequiredArgsConstructor
public class CardRepositoryCustomImpl implements CardRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public List<Card> findCardsWithImagesByUserId(Long userId) {
        return queryFactory
                .selectFrom(card)
                .leftJoin(card.cardImages, cardImage)
                .where(userIdEq(userId))
                .fetch();
    }

    @Override
    public Optional<Card> findByIdWithImages(UUID cardId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(card)
                .leftJoin(card.cardImages, cardImage)
                .where(cardIdEq(cardId))
                .fetchOne());
    }

    @Override
    public Optional<Card> findByIdWithUser(UUID userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(card)
                .leftJoin(card.user, user)
                .where(cardIdEq(userId))
                .fetchOne());
    }

    public BooleanExpression userIdEq(Long userId){
        return userId != null ? card.user.id.eq(userId) : null;
    }

    public BooleanExpression cardIdEq(UUID cardId){
        return cardId != null ? card.id.eq(cardId) : null;
    }
}
