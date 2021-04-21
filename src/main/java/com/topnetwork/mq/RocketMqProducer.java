package com.topnetwork.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * mq消息生产者
 *
 * @author Clgo
 * @date 2021/04/19 15:58
 **/
@Service
@Slf4j
public class RocketMqProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${ws.rocketmq.topic}")
    private String topic;

    /**
     * @param msgBody
     * @return
     **/
    public void sendMsg(String msgBody){
        log.info("开始消息发送:{}",msgBody);
        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build());
    }
}
