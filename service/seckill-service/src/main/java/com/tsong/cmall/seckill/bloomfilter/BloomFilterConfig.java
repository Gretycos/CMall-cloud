package com.tsong.cmall.seckill.bloomfilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

/**
 * @Author Tsong
 * @Date 2025/3/23 19:53
 */
@Configuration
public class BloomFilterConfig {
    @Value("${bloom-filter.expected-insertions}")
    private int expectedInsertions;
    @Value("${bloom-filter.fpp}")
    private double falsePositiveProbability;

    @Bean
    public BloomFilter<String> bloomFilter() {
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, falsePositiveProbability);
    }
}
