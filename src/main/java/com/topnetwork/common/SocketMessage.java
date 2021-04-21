package com.topnetwork.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息类封装
 *
 * @author Clgo
 * @date 2021/04/19 18:38
 **/
@Getter
@Setter
public class SocketMessage {
    private String topic;
    private String msg;

    @Override
    public String toString() {
        return "SocketMessage{" +
                "topic='" + topic + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
