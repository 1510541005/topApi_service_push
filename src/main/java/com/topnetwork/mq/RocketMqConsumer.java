package com.topnetwork.mq;

import com.topnetwork.websocket.NettySocketEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * rocketMq消费者
 * 在netty socketIo启动之后在启动
 * @author Clgo
 * @date 2021/04/23 09:12
 **/
@Component
@Slf4j
@Order(value = 2)
public class RocketMqConsumer implements CommandLineRunner {
    @Value("${rocketmq.name-server}")
    private String rocketMqServer;
    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;
    @Value("${ws.rocketmq.topic_block}")
    private String blockTopic;
    @Value("${ws.rocketmq.topic_transaction}")
    private String txTopic;
    @Value("${ws.rocketmq.topic_account}")
    private String accountTopic;

    @Resource
    private NettySocketEventHandler socketEventHandler;

    public void consumer(){
        try{
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(rocketMqServer);
            consumer.subscribe(blockTopic,"*");
            consumer.subscribe(txTopic,"*");
            consumer.subscribe(accountTopic,"*");
            consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                for (MessageExt message: list){
                    log.info("当前来源消息所属topic：{},消息内容：{}",message.getTopic(),list);
                    boolean flag = socketEventHandler.receiveMqMessage(message.getTopic(), message);
                    if (!flag){
                        log.info("消息topic={},msgId={} 投递失败",message.getTopic(),message.getMsgId());
                        result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        break;
                    }
                }
                return result;
            });
            consumer.start();
            log.info("RocketMQ consumer启动成功!");
        }catch (Exception e){
            log.info("RocketMQ consumer启动失败!");
        }
    }


    @Override
    public void run(String... args) {
        consumer();
    }
}
