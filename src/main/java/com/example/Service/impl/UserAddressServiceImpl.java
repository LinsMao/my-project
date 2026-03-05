package com.example.Service.impl;

import com.example.Entity.UserAddress;
import com.example.Mapper.UserAddressMapper;
import com.example.Service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getAddressList(Long userId) {
        return userAddressMapper.selectByUserId(userId);
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return userAddressMapper.selectDefaultByUserId(userId);
    }

    @Override
    public UserAddress getAddressById(Long id, Long userId) {
        UserAddress address = userAddressMapper.selectById(id);
        // 验证地址是否属于当前用户
        if (address != null && !address.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该地址");
        }
        return address;
    }

    @Override
    @Transactional
    public void addAddress(UserAddress address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault()) {
            userAddressMapper.cancelDefaultByUserId(address.getUserId());
        }
        
        userAddressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(UserAddress address, Long userId) {
        // 验证地址是否属于当前用户
        UserAddress existAddress = userAddressMapper.selectById(address.getId());
        if (existAddress == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!existAddress.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该地址");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault()) {
            userAddressMapper.cancelDefaultByUserId(userId);
        }

        userAddressMapper.update(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, Long userId) {
        // 验证地址是否属于当前用户
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该地址");
        }

        userAddressMapper.deleteById(id);

        // 如果删除的是默认地址，自动将第一个地址设为默认
        if (address.getIsDefault()) {
            List<UserAddress> addressList = userAddressMapper.selectByUserId(userId);
            if (!addressList.isEmpty()) {
                userAddressMapper.setDefault(addressList.get(0).getId(), userId);
            }
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long id, Long userId) {
        // 验证地址是否属于当前用户
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该地址");
        }

        // 先取消所有默认地址
        userAddressMapper.cancelDefaultByUserId(userId);
        
        // 设置新的默认地址
        userAddressMapper.setDefault(id, userId);
    }
}
