package com.topnetwork.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Clgo
 */
@Configuration
public class NettySocketIoConfig {

    @Value("${socket.hostname}")
    private String hostName;

    @Value("${socket.hostPort}")
    private Integer port;

    /**
     * netty-socketio服务器
     */
    @Bean
    public SocketIOServer socketServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(hostName);
        config.setPort(port);
        return new SocketIOServer(config);
    }

    /**
     * 用于扫描netty-socketio的注解，比如 @OnConnect、@OnEvent
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(socketServer());
    }
}
