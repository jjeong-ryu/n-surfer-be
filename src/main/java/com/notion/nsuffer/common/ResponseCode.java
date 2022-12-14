package com.notion.nsuffer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResponseCode {

    GET_CARD_LIST("Success", "C001", "Get card list", "모든 카드 조회"),
    POST_CARD("Success", "C002", "Post card list", "카드 작성"),
    GET_CARD("Success", "C003", "Get card detail", "카드 상세 조회"),
    UPDATE_CARD("Success", "C004", "update card", "카드 수정"),
    DELETE_CARD("Success", "C005", "delete card", "카드 삭제");

    private String status;
    private String code;
    private String message;
    @JsonIgnore
    private String description;
}
