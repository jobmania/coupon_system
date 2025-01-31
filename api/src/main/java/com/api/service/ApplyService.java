package com.api.service;

import com.api.domain.Coupon;
import com.api.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;

    public ApplyService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public void apply(Long userId){
        // 락을 활용한다면
        // 저장시 2초라면
        // 2초까지 걸림

        

        long count = couponRepository.count();

        if(count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
}
