package com.topnetwork;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.topnetwork.cache.ClientCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * 缓存测试
 *
 * @author Clgo
 * @date 2021/04/22 13:48
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TopApiServicePushMain.class)
public class TestClientCache {

    @Resource
    private ClientCache clientCache;
    
    @Test
    public void getUserClient(){
        HashMap<UUID, SocketIOClient> map = clientCache.getUserClient("zhangsan");
        System.out.println(JSON.toJSONString(map));
    }

    @Test
    public void getUserAllSub(){
        Set<String> subs = clientCache.getUserAllSub("zhangsan");
        System.out.println(JSON.toJSONString(subs));
    }

    @Test
    public void addSubToUser(){
        Long num = clientCache.addSubToUser("zhangsan", "sub_test");
        System.out.println(num);
    }

    @Test
    public void deleteAccount(){
        Boolean isDelete = clientCache.deleteAccount("zhangsan");
        System.out.println(isDelete);
        isDelete = clientCache.deleteAccount("lisi");
        System.out.println(isDelete);
        isDelete = clientCache.deleteAccount("wangwu");
        System.out.println(isDelete);
    }

    @Test
    public void deleteSubToUser(){
        Long num = clientCache.deleteSubToUser("zhangsan", "sub_block");
        System.out.println(num);
    }
    
    
}
