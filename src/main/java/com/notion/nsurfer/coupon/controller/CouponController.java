package com.notion.nsurfer.coupon.controller;

import com.notion.nsurfer.coupon.dto.GetCouponsDto;
import com.notion.nsurfer.coupon.service.CouponService;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponService;

//    public ResponseEntity<GetCouponsDto.Response> getCoupons(@AuthenticationPrincipal User user){
//
//    }
}
