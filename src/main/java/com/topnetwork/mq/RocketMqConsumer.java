package com.topnetwork.mq;

import com.alibaba.fastjson.JSONArray;
import com.topnetwork.common.SocketMessage;
import com.topnetwork.websocket.NettySocketEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mq消息接收
 * @author Clgo
 * @date 2021/04/19 15:48
 */
@Component
@RocketMQMessageListener(topic = "${ws.rocketmq.topic}", consumerGroup = "${rocketmq.consumer.group}",
        selectorExpression = "*")
@Slf4j
public class RocketMqConsumer implements RocketMQListener<MessageExt> {

    @Resource
    private NettySocketEventHandler handler;

    @Override
    public void onMessage(MessageExt message) {
        byte[] body = message.getBody();
        String msgContent = new String(body);
        SocketMessage socketMessage = JSONArray.parseObject(msgContent, SocketMessage.class);
        log.info("接收到消息 :{}", socketMessage);
        handler.sendBroadcastToSub(socketMessage.getTopic(),socketMessage);
    }
}
