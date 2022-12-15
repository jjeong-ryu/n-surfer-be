package com.notion.nsuffer.card.entity;

import com.notion.nsuffer.mypage.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id @GeneratedValue
    private Long id;

    private String title;

    private String content;

    private Date createDate;

    private Date lastEditDate;

    private String notionId;

    private String color;

    @OneToMany(mappedBy = "card")
    private List<CardLabel> cardLabels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
