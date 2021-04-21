package com.topnetwork.cache;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端数据缓存类
 *
 * @author Clgo
 * @date 2021/04/21 11:47
 **/
@Component
public class ClientCache {
    /**
     * 本地缓存 缓存用户-连接通道信息
     **/
    private static Map<String, HashMap<UUID, SocketIOClient>> concurrentHashMap = new ConcurrentHashMap<>();

    /**
     * 本地缓存 缓存用户-topic订阅信息
     **/
    private static Map<String, Set<String>> userSubscriptionMap = new ConcurrentHashMap<>();

    /**
     * 存入本地缓存
     * @param account 用户
     * @param sessionId 页面sessionId
     * @param socketIOClient 页面对应的通道连接信息
     */
    public void saveClient(String account, UUID sessionId, SocketIOClient socketIOClient){
        HashMap<UUID, SocketIOClient> sessionClientCache = concurrentHashMap.get(account);
        if (null == sessionClientCache){
            sessionClientCache = new HashMap<>();
        }
        sessionClientCache.put(sessionId, socketIOClient);
        concurrentHashMap.put(account, sessionClientCache);
    }

    /**
     * 根据用户获取所有通道信息
     * @param account
     * @return
     */
    public HashMap<UUID, SocketIOClient> getUserClient(String account){
        return concurrentHashMap.get(account);
    }

    /**
     * 根据用户及页面sessionId删除页面连接信息
     * @param account
     * @param sessionId
     * @return
     */
    public void deleteSessionClient(String account, UUID sessionId){
        concurrentHashMap.get(account).remove(sessionId);
    }

    /**
     * 给用户添加订阅信息
     * @param account
     * @param topic
     * @return
     */
    public void addSubToUser(String account, String topic){
        Set<String> subs = userSubscriptionMap.get(account);
        if (null == subs){
            subs = new HashSet<>();
        }
        subs.add(topic);
        userSubscriptionMap.put(account,subs);
    }

    /**
     * 用户取消订阅信息
     * @param account
     * @param topic
     * @return
     */
    public void deleteSubToUser(String account, String topic){
        userSubscriptionMap.get(account).remove(topic);
    }

    /**
     * 根据用户获取所有订阅信息
     * @param account
     * @return
     */
    public Set<String> getUserAllSub(String account){
        return userSubscriptionMap.get(account);
    }
}
