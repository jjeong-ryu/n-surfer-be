package com.notion.nsurfer.coupon.dto;


import lombok.Builder;
import lombok.Getter;

public class GetCouponsDto {
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
    }
}
