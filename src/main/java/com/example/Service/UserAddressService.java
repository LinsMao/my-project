package com.example.Service;

import com.example.Entity.UserAddress;

import java.util.List;

public interface UserAddressService {

    // 获取用户的所有地址
    List<UserAddress> getAddressList(Long userId);

    // 获取用户的默认地址
    UserAddress getDefaultAddress(Long userId);

    // 根据ID获取地址
    UserAddress getAddressById(Long id, Long userId);

    // 新增地址
    void addAddress(UserAddress address);

    // 更新地址
    void updateAddress(UserAddress address, Long userId);

    // 删除地址
    void deleteAddress(Long id, Long userId);

    // 设置默认地址
    void setDefaultAddress(Long id, Long userId);
}
