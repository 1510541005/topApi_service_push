package com.topnetwork.controller;

import com.alibaba.fastjson.JSONArray;
import com.topnetwork.common.SocketMessage;
import com.topnetwork.mq.RocketMqProducer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * mq消息控制层
 *
 * @author Clgo
 * @date 2021/04/19 16:28
 **/
@RestController
@RequestMapping("/mq")
public class RocketMqController {

    @Resource
    private RocketMqProducer producer;

    @RequestMapping("/sendMsg")
    public Object sendMg(@RequestParam(name = "topic") String topic,@RequestParam(name = "message") String msg){
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setTopic(topic);
        socketMessage.setMsg(msg);
        producer.sendMsg(JSONArray.toJSONString(socketMessage));
        return socketMessage+" 发送成功!";
    }
}
