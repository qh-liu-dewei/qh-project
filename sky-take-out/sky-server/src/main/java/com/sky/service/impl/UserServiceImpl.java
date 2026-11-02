package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j // 加上日志注解
@Service
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        log.info("微信登录，前端传来code：{}", userLoginDTO.getCode());
        // 向微信服务发送请求，获取openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");

        log.info("请求微信接口参数：{}", map);
        String s = HttpClientUtil.doGet(WX_LOGIN, map);
        log.info("微信返回原始字符串：{}", s);

        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");

        // 判断openid是否为null，若是则抛出业务异常
        if (openid == null){
            log.error("微信返回无openid，errmsg={}",jsonObject.getString("errmsg"));
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        // 若是完成自动注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            log.info("新用户注册成功，openid={}",openid);
        }
        // 返回对象
        return user;
    }
}
