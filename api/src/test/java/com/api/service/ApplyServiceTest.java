package com.api.service;

import com.api.repository.CouponRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;


    @Test
    public void 한번만응모() {
        // 요청이 여러개 들어오면 당연하게
        //
        applyService.apply(1L);

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명요청() throws InterruptedException {
        // 요청(1000개)이 여러개 들어오면 당연하게 멀티쓰레드
        // 100 개의 쿠폰.
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Thread.sleep(10000);

        long count = couponRepository.count();

        // 더 많은 쿠폰이 발급
        // 트랜잭션 (레이스 컨디션)
        assertThat(count).isEqualTo(100);
    }


    @Test
    public void 한명당_한개의쿠폰_발금() throws InterruptedException {
        // 요청(1000개)이 여러개 들어오면 당연하게 멀티쓰레드
        // 100 개의 쿠폰.
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            long userId = 1L;
            // 한개의 유저가 천개의 요청을 함.
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Thread.sleep(10000);

        long count = couponRepository.count();

        // 더 많은 쿠폰이 발급
        // 트랜잭션 (레이스 컨디션)
        assertThat(count).isEqualTo(1);
    }
}