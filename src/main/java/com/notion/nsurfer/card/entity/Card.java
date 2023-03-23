package com.notion.nsurfer.card.entity;

import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.Wave;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id @GeneratedValue
    private UUID id;

    private String notionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "card", cascade = CascadeType.PERSIST)
    private List<CardImage> cardImages;
}
