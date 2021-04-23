package com.topnetwork.cache;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private static final Map<String, HashMap<UUID, SocketIOClient>> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 使用redis缓存用户-topic订阅信息
     **/
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 存入本地缓存
     * @param account 用户
     * @param sessionId 页面sessionId
     * @param client 页面对应的通道连接信息
     */
    public void saveClient(String account, UUID sessionId, SocketIOClient client){
        HashMap<UUID, SocketIOClient> sessionClientCache = CONCURRENT_HASH_MAP.get(account);
        if (null == sessionClientCache){
            sessionClientCache = new HashMap<>();
        }
        sessionClientCache.put(sessionId, client);
        CONCURRENT_HASH_MAP.put(account, sessionClientCache);
    }

    /**
     * 根据用户获取所有通道信息
     * @param account 用户
     * @return 当前用户所有连接通道信息
     */
    public HashMap<UUID, SocketIOClient> getUserClient(String account){
        return CONCURRENT_HASH_MAP.get(account);
    }

    /**
     * 根据用户及页面sessionId删除页面连接信息
     * @param account 用户
     * @param sessionId 页面sessionId
     */
    public void deleteSessionClient(String account, UUID sessionId){
        CONCURRENT_HASH_MAP.get(account).remove(sessionId);
    }

    /**
     * 给用户添加订阅信息
     * @param account 用户
     * @param topic 订阅的消息topic
     * @return 返回添加成功的个数
     */
    public Long addSubToUser(String account, String topic){
        Boolean isMember = redisTemplate.opsForSet().isMember(account, topic);
        if (Boolean.FALSE.equals(isMember)){
            return redisTemplate.opsForSet().add(account,topic);
        }
        return 0L;
    }

    /**
     * 用户取消订阅信息
     * @param account 用户
     * @param topic 订阅的消息topic
     * @return 返回删除的个数
     */
    public Long deleteSubToUser(String account, String topic){
        return redisTemplate.opsForSet().remove(account,topic);
    }

    /**
     * 根据用户获取所有订阅信息
     * @param account 用户
     * @return 用户订阅的topic集合
     */
    public Set<String> getUserAllSub(String account){
        return redisTemplate.opsForSet().members(account);
    }

    /**
     * 删除用户
     * @param account 用户
     * @return 是否删除成功
     */
    public Boolean deleteAccount(String account){
        return redisTemplate.delete(account);
    }
}
