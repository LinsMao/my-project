package com.example.Service;

import com.example.DTO.admin.AdminLoginRequest;
import com.example.DTO.admin.AdminRegisterRequest;
import com.example.DTO.admin.AdminListRequest;
import com.example.DTO.admin.AdminUpdateProfileRequest;
import com.example.DTO.admin.AdminChangePasswordRequest;
import com.example.Entity.Admin;
import com.example.VO.AdminLoginVO;
import com.example.VO.AdminListVO;

import java.util.Map;

public interface AdminService {

    // 管理员/商家登录
    AdminLoginVO login(AdminLoginRequest request);

    //  管理员/商家注册
    void register(AdminRegisterRequest request);

    // 获取商家列表
    AdminListVO getMerchantList(AdminListRequest request);

    // 获取个人信息
    Admin getProfile(Long id);

    // 更新个人信息
    void updateProfile(AdminUpdateProfileRequest request);

    // 修改密码
    void changePassword(AdminChangePasswordRequest request);

    // 切换商家状态（启用/禁用）
    void toggleMerchantStatus(Long id, Integer status);

    // 商品审核
    void auditProduct(com.example.DTO.product.ProductAuditRequest request, Long adminId);

    // 获取待审核商品列表
    Map<String, Object> getPendingProducts(Integer page, Integer size);

    // 获取待审核商品列表（支持筛选）
    Map<String, Object> getPendingProductsWithFilter(Long productId, String productName, 
                                                      String startTime, String endTime,
                                                      Integer page, Integer size);

    /**
     * 获取商品详情（管理员用于审核，不限制商家权限）
     * @param productId 商品ID
     * @return 商品详情
     */
    com.example.VO.ProductDetailVO getProductDetailForAudit(Long productId);

    /**
     * 获取管理员的审核记录
     * @param auditorId 审核人ID
     * @param page 页码
     * @param size 每页数量
     * @return 审核记录列表和分页信息
     */
    Map<String, Object> getAdminAuditRecords(Long auditorId, Integer page, Integer size);
}