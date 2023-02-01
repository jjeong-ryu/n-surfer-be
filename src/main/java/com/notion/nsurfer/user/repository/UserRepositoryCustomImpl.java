package com.notion.nsurfer.user.repository;

import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.entity.QUser;
import com.notion.nsurfer.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.notion.nsurfer.user.entity.QUser.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public User findByEmail(String email) {
        return queryFactory
                .selectFrom(user)
                .where(userEmailEq(email))
                .fetchOne();
    }

    public BooleanExpression userEmailEq(String email){
        return !StringUtils.hasText(email) ? null : user.email.eq(email);
    }
}
