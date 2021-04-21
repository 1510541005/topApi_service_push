package com.topnetwork.controller;

import com.topnetwork.cache.ClientCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户控制层
 *
 * @author Clgo
 * @date 2021/04/21 12:03
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private ClientCache clientCache;

    /**
     * 获取用户当前所有订阅信息
     * @param account
     * @return
     * */
    @RequestMapping("/getUserAllSub")
    public Object getUserAllSub(@RequestParam(name = "account") String account){
        return clientCache.getUserAllSub(account);
    }
}
