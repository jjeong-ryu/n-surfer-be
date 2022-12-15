package com.notion.nsuffer.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class Label {

    @Id @GeneratedValue
    private Long id;

    private String color;

}
