package com.api.service;

import com.api.domain.Coupon;
import com.api.producer.CouponCreateProducer;
import com.api.repository.AppliedUserRepository;
import com.api.repository.CouponCountRepository;
import com.api.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;


    public ApplyService(CouponRepository couponRepository
            , CouponCountRepository couponCountRepository
            , CouponCreateProducer couponCreateProducer
            , AppliedUserRepository appliedUserRepository
    ) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    @Transactional
    public void apply(Long userId) {
        // 데이터베이스 락을 활용한다면
        // 저장시 2초라면 요청이 2초까지 걸림


        /** 레디스 (싱글 쓰레드)
         *  incr coupon_count : 쿠폰 갯수를 하나씩 증가.
         *
         * */

        // before : 데이터베이스 조회
//        long count = couponRepository.count();

        // after  2 : 레디스 <set> 구조를 사용하여 중복방지
        Long apply = appliedUserRepository.add(userId);

        if( apply != 1){
            return;
        }

        // after ; 레디스에서 조회
        Long count = couponCountRepository.increment();



        if (count > 100) {
            return;
        }
        // before : 데이터 베이스에서 직접 생성
//        couponRepository.save(new Coupon(userId));

        // after :
        couponCreateProducer.create(userId);
    }
}
