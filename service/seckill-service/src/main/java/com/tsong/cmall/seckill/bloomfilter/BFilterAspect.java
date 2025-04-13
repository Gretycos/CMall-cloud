package com.tsong.cmall.seckill.bloomfilter;

import com.google.common.hash.BloomFilter;
import com.tsong.cmall.common.exception.CMallException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Author Tsong
 * @Date 2025/3/23 20:05
 */
@Aspect
@Component
public class BFilterAspect {
    private BloomFilter<String> bloomFilter;

    public BFilterAspect(ApplicationContext applicationContext) {
        super();
        bloomFilter = applicationContext.getBean(BloomFilter.class);
    }

    @Pointcut("@annotation(com.tsong.cmall.seckill.bloomfilter.BFilter)")
    public void bFilterPointCut() {}

    @Before("bFilterPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        int i = 0;
        while (i < args.length) {
            if (parameterNames[i].equals("seckillId")) {
                break;
            }
            i++;
        }
        if (!bloomFilter.mightContain((String) args[i])) {
            CMallException.fail("非法请求");
        }
    }
}
