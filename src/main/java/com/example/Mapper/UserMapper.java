package com.example.Mapper;

import com.example.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByOpenid(@Param("openid") String openid);

    int insert(User user);

    int updateProfile(User user);

    int updateLastLogin(@Param("id") Long id);
}
