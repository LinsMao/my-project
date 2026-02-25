package com.example.Service;

import com.example.DTO.RegisterRequest;
import com.example.VO.LoginVO;

import java.util.List;


public interface UserService {
    /**
     * 微信登录
     */
    LoginVO wxLogin(String code);


    /**
     * 注册用户信息
     */
    void register(Long userId,  RegisterRequest request);

}
