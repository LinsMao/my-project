package com.example.Mapper;

import com.example.Entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    // 查询用户的所有地址
    List<UserAddress> selectByUserId(@Param("userId") Long userId);

    // 根据ID查询地址
    UserAddress selectById(@Param("id") Long id);

    // 查询用户的默认地址
    UserAddress selectDefaultByUserId(@Param("userId") Long userId);

    // 新增地址
    int insert(UserAddress address);

    // 更新地址
    int update(UserAddress address);

    // 删除地址
    int deleteById(@Param("id") Long id);

    // 取消用户的所有默认地址
    int cancelDefaultByUserId(@Param("userId") Long userId);

    // 设置默认地址
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);
}
