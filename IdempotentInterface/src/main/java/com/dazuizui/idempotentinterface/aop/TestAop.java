package com.dazuizui.idempotentinterface.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TestAop {
    @Autowired
    private RedisTemplate redisTemplate;

    @Before("execution(* com.dazuizui.idempotentinterface.controller.TestController.sub(..))")
    public void befor(JoinPoint proceedingJoinPoint) throws Exception {
        Object[] args = proceedingJoinPoint.getArgs();
        boolean b = redisTemplate.delete(args[0]);
        System.out.println(b);
        if (!b){
            //todo 日志操作
            throw new Exception("幂等性操作");
        }
    }
}
