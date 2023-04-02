package com.notion.nsurfer.card.entity;

import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.Wave;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "card", cascade = CascadeType.PERSIST)
    private List<CardImage> cardImages;

    @CreatedDate
    private LocalDateTime createdAt;

}
