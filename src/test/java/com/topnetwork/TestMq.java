package com.topnetwork;

import com.topnetwork.mq.RocketMqProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * mq消息测试
 *
 * @author Clgo
 * @date 2021/04/19 16:14
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TopApiServicePushMain.class)
public class TestMq {

    @Autowired
    private RocketMqProducer producer;

    @Test
    public void testSendMsg(){
        producer.sendMsg("发送消息测试");
    }
}
