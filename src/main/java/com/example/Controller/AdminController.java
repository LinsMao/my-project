package com.example.Controller;


import com.example.Common.ApiResponse;
import com.example.DTO.admin.AdminLoginRequest;
import com.example.DTO.admin.AdminRegisterRequest;
import com.example.DTO.admin.AdminListRequest;
import com.example.DTO.admin.AdminUpdateProfileRequest;
import com.example.DTO.admin.AdminChangePasswordRequest;
import com.example.Entity.Admin;
import com.example.Service.AdminService;
import com.example.VO.AdminLoginVO;
import com.example.VO.AdminListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @PostMapping("/login")
    public ApiResponse<AdminLoginVO> login(@Valid @RequestBody AdminLoginRequest request) {
        try {
            AdminLoginVO vo = adminService.login(request);
            return ApiResponse.success(vo);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }


    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody AdminRegisterRequest request) {
        try {
            adminService.register(request);
            return ApiResponse.success("注册成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }


    @GetMapping("/merchants")
    public ApiResponse<AdminListVO> getMerchantList(@Valid AdminListRequest request) {
        try {
            AdminListVO vo = adminService.getMerchantList(request);
            return ApiResponse.success(vo);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }


    @GetMapping("/profile/{id}")
    public ApiResponse<Admin> getProfile(@PathVariable Long id) {
        try {
            Admin admin = adminService.getProfile(id);
            return ApiResponse.success(admin);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }


    @PutMapping("/profile")
    public ApiResponse<String> updateProfile(@Valid @RequestBody AdminUpdateProfileRequest request) {
        try {
            adminService.updateProfile(request);
            return ApiResponse.success("更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }


    @PutMapping("/password")
    public ApiResponse<String> changePassword(@Valid @RequestBody AdminChangePasswordRequest request) {
        try {
            adminService.changePassword(request);
            return ApiResponse.success("密码修改成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("修改失败：" + e.getMessage());
        }
    }


    @PutMapping("/merchant/{id}/status")
    public ApiResponse<String> toggleMerchantStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        try {
            adminService.toggleMerchantStatus(id, status);
            return ApiResponse.success("操作成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 商品审核
     */
    @PostMapping("/product/audit")
    public ApiResponse<String> auditProduct(
            @RequestBody com.example.DTO.product.ProductAuditRequest request,
            @RequestParam Long adminId
    ) {
        try {
            adminService.auditProduct(request, adminId);
            return ApiResponse.success("审核成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("审核失败：" + e.getMessage());
        }
    }

    /**
     * 获取待审核商品列表
     */
    @GetMapping("/products/pending")
    public ApiResponse<Map<String, Object>> getPendingProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        try {
            Map<String, Object> result = adminService.getPendingProducts(page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取待审核商品失败：" + e.getMessage());
        }
    }

    /**
     * 获取待审核商品列表（支持筛选）
     */
    @GetMapping("/products/pending-filter")
    public ApiResponse<Map<String, Object>> getPendingProductsWithFilter(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        try {
            Map<String, Object> result = adminService.getPendingProductsWithFilter(
                productId, productName, startTime, endTime, page, size
            );
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取待审核商品失败：" + e.getMessage());
        }
    }

    /**
     * 获取商品详情（管理员用于审核）
     * @param productId 商品ID
     * @return 商品详情
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<com.example.VO.ProductDetailVO> getProductDetail(@PathVariable Long productId) {
        try {
            com.example.VO.ProductDetailVO detail = adminService.getProductDetailForAudit(productId);
            return ApiResponse.success(detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取商品详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取管理员的审核记录
     * @param auditorId 审核人ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 审核记录列表和分页信息
     */
    @GetMapping("/audit-records")
    public ApiResponse<Map<String, Object>> getAdminAuditRecords(
            @RequestParam Long auditorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        try {
            Map<String, Object> result = adminService.getAdminAuditRecords(auditorId, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取审核记录失败：" + e.getMessage());
        }
    }
}