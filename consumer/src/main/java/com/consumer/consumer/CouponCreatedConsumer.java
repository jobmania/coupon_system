package com.consumer.consumer;

import com.consumer.domain.Coupon;
import com.consumer.domain.FailedEvent;
import com.consumer.repository.CouponRepository;
import com.consumer.repository.FailedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponCreatedConsumer {
    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;
    private final Logger logger = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    public CouponCreatedConsumer(CouponRepository couponRepository
    , FailedEventRepository failedEventRepository) {
        this.couponRepository = couponRepository;
        this.failedEventRepository = failedEventRepository;
    }

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId){
        try {
            couponRepository.save(new Coupon(userId));
            System.out.println(userId);
        } catch (Exception e) {
            logger.error("failed to crate coupon : " + userId);
            logger.error(e.getMessage());
            failedEventRepository.save(new FailedEvent(userId));
        }

    }
}
