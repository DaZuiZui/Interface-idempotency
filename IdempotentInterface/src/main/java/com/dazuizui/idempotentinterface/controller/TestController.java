package com.dazuizui.idempotentinterface.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/getToken")
    public String getToken(){
        String token = UUID.randomUUID().toString().substring(0,8);
        redisTemplate.opsForValue().set(token,"1",60*10, TimeUnit.SECONDS);
        return token;
    }

    @GetMapping("/sub")
    public void sub(String token) throws InterruptedException {
        //todo 业务操作
    }
}
