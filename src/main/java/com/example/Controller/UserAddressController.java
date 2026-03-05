package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Entity.UserAddress;
import com.example.Service.UserAddressService;
import com.example.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "*")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取地址列表
     */
    @GetMapping("/list")
    public ApiResponse<List<UserAddress>> getAddressList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            List<UserAddress> addressList = userAddressService.getAddressList(userId);
            return ApiResponse.success(addressList);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public ApiResponse<UserAddress> getDefaultAddress(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            UserAddress address = userAddressService.getDefaultAddress(userId);
            return ApiResponse.success(address);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取地址详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UserAddress> getAddressById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            UserAddress address = userAddressService.getAddressById(id, userId);
            if (address == null) {
                return ApiResponse.error("地址不存在");
            }
            return ApiResponse.success(address);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 新增地址
     */
    @PostMapping("/add")
    public ApiResponse<?> addAddress(@RequestBody UserAddress address, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            address.setUserId(userId);
            userAddressService.addAddress(address);
            return ApiResponse.success("添加成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新地址
     */
    @PutMapping("/update")
    public ApiResponse<?> updateAddress(@RequestBody UserAddress address, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            userAddressService.updateAddress(address, userId);
            return ApiResponse.success("修改成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            userAddressService.deleteAddress(id, userId);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/setDefault/{id}")
    public ApiResponse<?> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ApiResponse.unauthorized("未登录");
        }

        try {
            userAddressService.setDefaultAddress(id, userId);
            return ApiResponse.success("设置成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 获取当前用户ID
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return null;
        }
        return JwtUtils.getUserIdFromToken(token);
    }
}
