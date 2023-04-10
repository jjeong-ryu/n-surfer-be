package com.notion.nsurfer.coupon.repository;

import com.notion.nsurfer.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
}
