package com.notion.nsuffer.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseCode {

    // Card
    GET_CARD_LIST("Success", "C001", "Get card list", "모든 카드 조회"),
    POST_CARD("Success", "C002", "Post card list", "카드 작성"),
    GET_CARD("Success", "C003", "Get card detail", "카드 상세 조회"),
    UPDATE_CARD("Success", "C004", "update card", "카드 수정"),
    DELETE_CARD("Success", "C005", "delete card", "카드 삭제"),

    //Error - Card
    CARD_NOT_FOUND("Failure", "EC001", "Card not Found", "해당 카드가 존재하지 않습니다."),

    // DbSync
    DB_SYNC("Success", "DS001", "DB sync", "노션DB와 n-suffer의 싱크 맞추기"),

    // User
    SIGN_UP("Success", "U001", "USER SING UP", "유저 회원가입");

    // Error - User
    private String status;
    private String code;
    private String message;
    @JsonIgnore
    private String description;
}
