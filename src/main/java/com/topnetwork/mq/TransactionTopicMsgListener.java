package com.topnetwork.mq;

import com.topnetwork.websocket.NettySocketEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mq区块消息接收
 *
 * @author Clgo
 * @date 2021/04/22 14:53
 **/
@Component
@RocketMQMessageListener(topic = "${ws.rocketmq.topic_transaction}", consumerGroup = "${ws.rocketmq.group_transaction}")
@Slf4j
public class TransactionTopicMsgListener implements RocketMQListener<MessageExt> {

    @Resource
    private NettySocketEventHandler handler;
    @Value("${ws.rocketmq.topic_transaction}")
    private String topic;

    @Override
    public void onMessage(MessageExt messageExt) {
        handler.receiveMqMessage(topic,messageExt);
    }
}
