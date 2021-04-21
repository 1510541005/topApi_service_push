package com.topnetwork.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Socket启动
 * @author Clgo
 * @date 2021/04/19 13:35
 */
@Component
@Slf4j
public class NettySocketApplicationRunner implements CommandLineRunner {
    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public void run(String... args) throws Exception {
        socketIOServer.start();
        log.info("socket.io启动成功！");
    }
}
