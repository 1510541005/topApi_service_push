package com.topnetwork.websocket;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.topnetwork.cache.ClientCache;
import com.topnetwork.common.SocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * 消息处理器
 * @author Clgo
 * @date 2021/04/19 13:35
 */
@Component
@Slf4j
public class NettySocketEventHandler {

    @Resource
    private SocketIOServer socketIoServer;

    @Resource
    private ClientCache clientCache;

    @Value("${socket.urlParam}")
    private String urlParam;

    /**
     * 客户端连接的时候触发
     *
     * @param client 页面对应的通道连接信息
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        String param = client.getHandshakeData().getSingleUrlParam(urlParam);
        String sa = client.getRemoteAddress().toString();
        String clientIp = sa.substring(1, sa.indexOf(":"));
        //存储SocketIOClient，用于发送消息
        UUID sessionId = client.getSessionId();
        clientCache.saveClient(param,sessionId,client);
        //查看用户是否有已订阅信息
        Set<String> userAllSub = clientCache.getUserAllSub(param);
        if (userAllSub!=null && !userAllSub.isEmpty()){
            for(String topic : userAllSub){
                //将已订阅信息加入到房间中
                client.joinRoom(topic);
            }
        }
        log.info("clientIp:{},socketSessionId:{},客户端已连接",clientIp, sessionId);
        client.sendEvent("advert_info",clientIp+" 客户端你好，我是服务端，有什么能帮助你的？");
        //将客户订阅信息返回给客户端
        String msg = "当前用户["+param+"]订阅的服务有:"+ JSON.toJSONString(userAllSub);
        if (null == userAllSub || userAllSub.isEmpty()){
            msg = "当前用户["+param+"]无订阅服务";
        }
        client.sendEvent("subscription_info",msg);
    }

    /**
     * 客户端关闭连接时触发
     * @param client 页面对应的通道连接信息
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String param = client.getHandshakeData().getSingleUrlParam(urlParam);
        String sa = client.getRemoteAddress().toString();
        String clientIp = sa.substring(1, sa.indexOf(":"));
        //移除客户端实例
        clientCache.deleteSessionClient(param,client.getSessionId());
        log.info("clientIp:{},socketSessionId:{},客户端已断开连接",clientIp,client.getSessionId());
        //回复客户端消息
        client.sendEvent("advert_info","客户端你好，我是服务端，期待下次和你见面");
    }

    /**
     * 消息订阅
     * @date 2021/04/20 14:06
     * @param client 页面对应的通道连接信息
     * @param request 应答
     * @param channel 通道
     **/
    @OnEvent("sub")
    public void topSub(SocketIOClient client,AckRequest request,String channel){
        log.info("当前sessionId={}，是否应答请求={},channel={}",client.getSessionId(),request.isAckRequested(),channel);
        String param = client.getHandshakeData().getSingleUrlParam(urlParam);
        String sa = client.getRemoteAddress().toString();
        String clientIp = sa.substring(1, sa.indexOf(":"));
        log.info("client sub,channel:{}, socketSessionId:{}, ip:{}",channel,client.getSessionId(),clientIp);
        String[] channels = channel.split(",");
        for (String topic :channels){
            client.joinRoom(topic);
            clientCache.addSubToUser(param,topic);
        }
        log.info("after client sub，allRooms:{}", client.getAllRooms());
        //回复客户端消息
        client.sendEvent("advert_info","客户端你好，我是服务端,你已成功订阅["+channel+"]");
    }

    /**
     * 取消订阅
     * @date 2021/04/20 14:06
     * @param client 页面对应的通道连接信息
     * @param request 应答
     * @param channel 通道
     **/
    @OnEvent("unsub")
    public void topUnSub(SocketIOClient client, AckRequest request, String channel){
        log.info("当前sessionId={}，是否应答请求={},channel={}",client.getSessionId(),request.isAckRequested(),channel);
        String param = client.getHandshakeData().getSingleUrlParam(urlParam);
        String sa = client.getRemoteAddress().toString();
        String clientIp = sa.substring(1, sa.indexOf(":"));
        log.info("client unsub,channel:{},socketSessionId:{},ip:{}", channel, client.getSessionId(), clientIp);
        String[] channels = channel.split(",");
        for (String topic :channels){
            client.leaveRoom(topic);
            clientCache.deleteSubToUser(param,topic);
        }
        log.info("after client unsub，allRooms:{}", Arrays.asList(client.getAllRooms()));
        //回复客户端消息
        client.sendEvent("advert_info","客户端你好，我是服务端,你已取消["+channel+"]订阅");
    }
    /**
     * 获取用户下已订阅的服务
     * @date 2021/04/20 14:06
     * @param client 页面对应的通道连接信息
     * @param request 应答
     * @param channel 通道
     **/
    @OnEvent("getUserAllSub")
    public void getUserAllSub(SocketIOClient client, AckRequest request, String channel){
        log.info("当前sessionId={}，是否应答请求={},channel={}",client.getSessionId(),request.isAckRequested(),channel);
        String param = client.getHandshakeData().getSingleUrlParam(urlParam);
        Set<String> userAllSub = clientCache.getUserAllSub(param);
        log.info("当前用户={},订阅的服务有:{}", param, JSON.toJSONString(userAllSub));
        //将客户订阅信息返回给客户端
        String msg = "当前用户["+param+"]订阅的服务有:"+ JSON.toJSONString(userAllSub);
        if (null == userAllSub || userAllSub.isEmpty()){
            msg = "当前用户["+param+"]无订阅服务";
        }
        client.sendEvent("subscription_info",msg);
    }

    /**
     * 消息广播
     * @author Clgo
     * @date 2021/04/20 15:00
     * @param channel 消息通道
     * @param message 对应发送的消息
     **/
    public void sendBroadcastToSub(String channel, SocketMessage message) {
        socketIoServer.getRoomOperations(channel).sendEvent(channel,message);
    }

    /**
     * 消息广播
     * @author Clgo
     * @date 2021/04/20 15:00
     * @param topic 消息主题
     * @param messageExt 消息体
     **/
    public void receiveMqMessage(String topic, MessageExt messageExt){
        byte[] body = messageExt.getBody();
        String msgContent = new String(body);
        log.info("接收到{}消息 :{}",topic, msgContent);
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setTopic(topic);
        socketMessage.setMsg(msgContent);
        sendBroadcastToSub(topic,socketMessage);
    }
}
