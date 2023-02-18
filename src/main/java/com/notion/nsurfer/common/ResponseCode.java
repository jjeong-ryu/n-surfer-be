package com.notion.nsurfer.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    UPDATE_USER_PROFILE("Success", "U003", "Update user profile", "유저 프로필 업데이트"),
    DELETE_USER("Success", "U003", "Delete User", "유저 회원탈퇴"),
    MAKE_NEW_ACCESS_TOKEN("Success", "U004", "Make new access token", "새로운 액세스 토큰 발급" ),
    MAKE_NEW_ACCESS_AND_REFRESH_TOKEN("Success", "U005", "Make new access token and refresh token", "새로운 액세스 토큰 및 리프레시 발급" ),

    // Error - User
    ERROR_UNAUTHENTICATED("Failure", "EU001", "Authentication fail", "인증 실패"),
    ERROR_USER_NOT_FOUND("Failure", "EU002", "User id not found", "해당 유저가 존재하지 않음"),
    ERROR_INVALID_ACCESS_TOKEN("Failure", "EU003", "Invalid access token", "액세스 토큰이 유효하지 않음"),
    ERROR_EXPIRED_ACCESS_TOKEN("Failure", "EU004", "Expired access Token", "액세스 토큰이 만료됨"),
    ERROR_INVALID_REFRESH_TOKEN("Failure", "EU005", "Invalid access token", "리프레시 토큰이 유효하지 않음"),
    ERROR_EXPIRED_REFRESH_TOKEN("Failure", "EU006", "Expired refresh Token", "리프레시 토큰이 만료됨"),


    ERROR_EMAIL_NOT_FOUND("Failure", "EU003", "Email not found", "존재하지 않는 이메일입니다.");

    private String status;
    private String code;
    @Setter
    private String message;
    @JsonIgnore
    private String description;
}
