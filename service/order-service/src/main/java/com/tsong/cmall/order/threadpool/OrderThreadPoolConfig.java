package com.tsong.cmall.order.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Tsong
 * @Date 2025/3/22 02:06
 */
@Configuration
public class OrderThreadPoolConfig {

    @Value("${executor.core-pool-size}")
    int corePoolSize;

    @Value("${executor.max-pool-size}")
    int maxPoolSize;

    @Value("${executor.keep-alive-time}")
    int keepAliveTime;

    @Value("${executor.blocking-queue-size}")
    int blockingQueueSize;

    @Bean("orderThreadPool")
    public ThreadPoolExecutor orderThreadPool() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(blockingQueueSize),
                new NamingThreadFactory("Order-Thread"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

   static final class NamingThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNum = new AtomicInteger();
        private final String name;

        public NamingThreadFactory(String name) {
            this.name = name;
        }
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(name + " [#" + threadNum.incrementAndGet() + "]");
            return t;
        }
    }
}
