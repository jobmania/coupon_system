package com.api.service;

import com.api.domain.Coupon;
import com.api.repository.CouponCountRepository;
import com.api.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;
    private final CouponCountRepository cuponCountRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository) {
        this.couponRepository = couponRepository;
        this.cuponCountRepository = couponCountRepository;
    }

    @Transactional
    public void apply(Long userId){
        // 데이터베이스 락을 활용한다면
        // 저장시 2초라면 요청이 2초까지 걸림


        /** 레디스 (싱글 쓰레드)
         *  incr coupon_count : 쿠폰 갯수를 하나씩 증가.
         *
         * */

        // 데이터베이스
//        long count = couponRepository.count();

        // 레디스
        Long count = cuponCountRepository.increment();

        if(count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
}
