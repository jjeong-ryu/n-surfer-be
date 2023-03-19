package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.entity.QCard;
import com.notion.nsurfer.card.entity.QCardImage;
import com.notion.nsurfer.user.entity.QWave;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.notion.nsurfer.card.entity.QCard.*;
import static com.notion.nsurfer.card.entity.QCardImage.*;
import static com.notion.nsurfer.user.entity.QWave.*;

@Repository
@RequiredArgsConstructor
public class CardRepositoryCustomImpl implements CardRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public List<Card> findCardsWithWaveByUserId(Long userId) {
        return queryFactory
                .selectFrom(card)
//                .leftJoin(card.waves, wave)
                .where(userIdEq(userId))
                .fetch();
    }

    @Override
    public Optional<Card> findByIdWithImages(Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(card)
                .leftJoin(card.cardImages, cardImage)
                .fetchOne());
    }

    public BooleanExpression userIdEq(Long userId){
        return userId != null ? card.user.id.eq(userId) : null;
    }
}
