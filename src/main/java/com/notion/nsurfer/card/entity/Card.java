package com.notion.nsurfer.card.entity;

import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.Wave;
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

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEditDate;

    private String notionId;

    private String color;

    @OneToMany(mappedBy = "card")
    private List<CardLabel> cardLabels;

    @OneToMany(mappedBy = "card")
    private List<Wave> waves;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
