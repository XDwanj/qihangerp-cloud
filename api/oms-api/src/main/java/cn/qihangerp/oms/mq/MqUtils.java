package cn.qihangerp.oms.mq;//package com.qihang.sys.api.mq;
//
//import lombok.AllArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@AllArgsConstructor
//@Component
//public class MqUtils {
//    private final RedisTemplate redisTemplate;
//
//    public void sendMessage(String channel, Object message) {
//        redisTemplate.convertAndSend(channel, message);
//    }
//    public void sendApiMessage(MqMessage message) {
//        redisTemplate.convertAndSend("ApiMessage", message);
//    }
//}
