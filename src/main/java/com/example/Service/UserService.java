package com.example.Service;

import com.example.DTO.RegisterRequest;
import com.example.DTO.user.UserListRequest;
import com.example.Entity.User;
import com.example.VO.LoginVO;
import com.example.VO.UserListVO;

public interface UserService {

    // 小程序端用户登录注册
    LoginVO wxLogin(String code);
    void register(Long userId, RegisterRequest request);

    // 后台管理端用户管理
    UserListVO getUserList(UserListRequest request);
    User getUserById(Long id);
    void toggleUserStatus(Long id, Integer status);
}
