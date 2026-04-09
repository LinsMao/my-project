package com.example.Service.impl;

import com.example.DTO.RegisterRequest;
import com.example.DTO.user.UserListRequest;
import com.example.Entity.User;
import com.example.Mapper.UserMapper;
import com.example.Service.UserService;
import com.example.Utils.JwtUtils;
import com.example.VO.LoginVO;
import com.example.VO.UserListVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // ==================== 小程序端用户登录注册 ====================

    @Override
    @Transactional
    public LoginVO wxLogin(String code) {
        // 调用微信接口获取openid
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
            throw new RuntimeException("解析微信返回数据失败: " + responseStr, e);
        }

        if (response == null || response.errcode != null && response.errcode != 0) {
            throw new RuntimeException("微信登录失败：" + (response == null ? "未知错误" : response.errmsg));
        }

        String openid = response.openid;
        String sessionKey = response.session_key;
        String unionid = response.unionid;

        // 查询用户
        User user = userMapper.findByOpenid(openid);
        boolean isNewUser = false;

        if (user == null) {
            // 新用户
            user = new User();
            user.setOpenid(openid);
            user.setSessionKey(sessionKey);
            user.setUnionid(unionid);
            userMapper.insert(user);
            isNewUser = true;
        } else {
            // 老用户，更新 session_key 和登录时间
            user.setSessionKey(sessionKey);
            user.setUnionid(unionid);
            userMapper.updateLastLogin(user.getId());
        }

        // 生成 JWT token
        String token = JwtUtils.generateToken(user.getId());

        // 返回数据
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setIsNewUser(isNewUser);

        if (!isNewUser) {
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
        user.setAvatar(request.getAvatar());
        user.setGender(request.getGender());
        userMapper.updateProfile(user);
    }

    // ==================== 后台管理端用户管理 ====================

    @Override
    public UserListVO getUserList(UserListRequest request) {
        // 计算 OFFSET
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        request.setPageNum(offset);

        // 查询列表
        List<User> userList = userMapper.findByConditions(request);

        // 查询总数
        Long total = userMapper.countByConditions(request);

        // 转换为 VO
        List<UserListVO.UserItem> items = userList.stream().map(user -> {
            UserListVO.UserItem item = new UserListVO.UserItem();
            item.setId(user.getId());
            item.setNickname(user.getNickname());
            item.setAvatar(user.getAvatar());
            item.setGender(user.getGender());
            item.setPhone(user.getPhone());
            item.setStatus(user.getStatus());
            item.setCreatedAt(user.getCreatedAt());
            item.setLastLoginAt(user.getLastLoginAt());
            return item;
        }).collect(Collectors.toList());

        UserListVO vo = new UserListVO();
        vo.setTotal(total);
        vo.setList(items);

        return vo;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id, Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 更新状态
        userMapper.updateStatus(id, status);
    }
}
