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

    private String notionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "card", cascade = CascadeType.PERSIST)
    private List<CardImage> cardImages;
}
