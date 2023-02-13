package com.songhaozhi.mayday.mq;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(consumerGroup = "xxxGroup",
        topic = "testTopic",
        selectorExpression = "2||3||4",//tag
        consumeThreadMax = 3,//ORDERLY : 1
        consumeMode = ConsumeMode.ORDERLY)
@Component
public class ConsumerListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("接收到消息：" + message);
    }

}