package com.example.Service.impl;

import com.example.DTO.RegisterRequest;
import com.example.Entity.User;
import com.example.Mapper.UserMapper;
import com.example.Service.UserService;
import com.example.VO.LoginVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.example.Utils.JwtUtils;
import com.example.Utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.mini.appid}")
    private String appid;

    @Value("${wx.mini.secret}")
    private String secret;


    @Value("file:D:/my-images/users/")
    private String avatarLocalPath;

    @Value("${app.image.users-path}")
    private String avatarWebPath;

    @Value("${app.base-url}")
    private String baseUrl;




    // 微信返回数据封装
    private static class WxSessionResponse {
        public String openid;
        public String session_key;
        public String unionid;
        public Integer errcode;
        public String errmsg;
    }

    @Override
    @Transactional
    public LoginVO wxLogin(String code) {

        //调用微信接口获取openid
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("code", code);


        String responseStr = restTemplate.getForObject(url, String.class, params);

        // 手动使用 Jackson 解析 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        WxSessionResponse response;
        try {
             response = objectMapper.readValue(responseStr, WxSessionResponse.class);
        } catch (JsonProcessingException e) {
            // 记录日志并抛出运行时异常，避免污染上层
            throw new RuntimeException("解析微信返回数据失败: " + responseStr, e);
        }

        if (response == null || response.errcode != null && response.errcode != 0) {
            throw new RuntimeException("微信登录失败：" + (response == null ? "未知错误" : response.errmsg));
        }

        String openid = response.openid;
        String sessionKey = response.session_key;
        String unionid = response.unionid;

        //查询用户
        User user = userMapper.findByOpenid(openid);
        boolean isNewUser = false;

        if(user==null){
            //新用户
            user=new User();
            user.setOpenid(openid);
            user.setSessionKey(sessionKey);
            user.setUnionid(unionid);

            userMapper.insert(user);
            isNewUser = true;

        }else {
            // 老用户，更新 session_key 和登录时间
            user.setSessionKey(sessionKey);
            user.setUnionid(unionid);
            userMapper.updateLastLogin(user.getId());
        }

        // 生成 JWT token
        String token = JwtUtils.generateToken(user.getId());

        //返回数据
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setIsNewUser(isNewUser);

        if (!isNewUser){
            // 老用户返回用户信息
            LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setGender(user.getGender());
            vo.setUserInfo(userInfo);
        }

        return vo;



    }

    @Override
    @Transactional
    public void register(Long userId, RegisterRequest request) {

        // 更新用户信息
        User user = new User();
        user.setId(userId);
        user.setNickname(request.getNickname());
        user.setAvatar(request.getAvatar()); // 存入完整 URL
        user.setGender(request.getGender());
        userMapper.updateProfile(user);
    }


}
