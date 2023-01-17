package com.notion.nsurfer.common;

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
    DB_SYNC("Success", "DS001", "DB sync", "notion DB와 n-surfer의 싱크 맞추기"),

    // User
    SIGN_UP("Success", "U001", "User sign up", "유저 회원가입"),
    GET_USER_PROFILE("Success", "U002", "Get user profile", "유저 프로필 조회"),

    // Error - User
    ERROR_UNAUTHENTICATED("Failure", "EU001", "Authentication fail", "인증 실패"),
    ERROR_EMAIL_NOT_FOUND("Failure", "EU002", "Email not found", "존재하지 않는 이메일입니다.");

    private String status;
    private String code;
    private String message;
    @JsonIgnore
    private String description;
}
