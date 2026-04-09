package com.example.Mapper;

import com.example.DTO.user.UserListRequest;
import com.example.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByOpenid(@Param("openid") String openid);

    User findById(@Param("id") Long id);

    int insert(User user);

    int updateProfile(User user);

    int updateLastLogin(@Param("id") Long id);

    // 根据条件查询用户列表
    List<User> findByConditions(UserListRequest request);

    // 统计符合条件的总数
    Long countByConditions(UserListRequest request);

    // 更新用户状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
