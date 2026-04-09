package com.example.Service.impl;

import com.example.DTO.admin.AdminLoginRequest;
import com.example.DTO.admin.AdminRegisterRequest;
import com.example.DTO.admin.AdminListRequest;
import com.example.DTO.admin.AdminUpdateProfileRequest;
import com.example.DTO.admin.AdminChangePasswordRequest;
import com.example.DTO.product.ProductAuditRequest;
import com.example.Entity.Admin;
import com.example.Entity.Product;
import com.example.Entity.ProductAudit;
import com.example.Mapper.admin.AdminMapper;
import com.example.Mapper.ProductMapper;
import com.example.Mapper.ProductAuditMapper;
import com.example.Service.AdminService;
import com.example.Utils.JwtUtils;
import com.example.VO.AdminLoginVO;
import com.example.VO.AdminListVO;

import com.example.VO.ProductAuditVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductAuditMapper productAuditMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public AdminLoginVO login(AdminLoginRequest request) {

        // 查询用户
        Admin admin = adminMapper.findByUsername(request.getUsername());

        // 校验
        if (admin == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new IllegalArgumentException("账号已被禁用");
        }


        // 更新最后登录时间
        adminMapper.updateLastLoginTime(admin.getId());

        String token = JwtUtils.generateToken(admin.getId());


        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(token);

        AdminLoginVO.AdminInfo adminInfo = new AdminLoginVO.AdminInfo();
        adminInfo.setId(admin.getId());
        adminInfo.setUsername(admin.getUsername());
        adminInfo.setNickname(admin.getNickname() != null ? admin.getNickname() : admin.getUsername());
        adminInfo.setAvatar(admin.getAvatar());
        adminInfo.setRole(admin.getRole());
        vo.setAdminInfo(adminInfo);

        return vo;
    }

    @Override
    @Transactional
    public void register(AdminRegisterRequest request) {
        Admin existAdmin = adminMapper.findByUsername(request.getUsername());
        if (existAdmin != null) {
            throw new IllegalArgumentException("用户名已存在");
        }


        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Admin existEmail = adminMapper.findByEmail(request.getEmail());
            if (existEmail != null) {
                throw new IllegalArgumentException("邮箱已被注册");
            }
        }


        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setEmail(request.getEmail());
        admin.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        admin.setRole(1);  //默认为商家
        admin.setStatus(1);

        adminMapper.insert(admin);

    }

    @Override
    public AdminListVO getMerchantList(AdminListRequest request) {
        // 计算 OFFSET
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        request.setPageNum(offset);

        // 查询列表
        List<Admin> adminList = adminMapper.findByConditions(request);

        // 查询总数
        Long total = adminMapper.countByConditions(request);

        // 转换为 VO
        List<AdminListVO.AdminItem> items = adminList.stream().map(admin -> {
            AdminListVO.AdminItem item = new AdminListVO.AdminItem();
            item.setId(admin.getId());
            item.setUsername(admin.getUsername());
            item.setEmail(admin.getEmail());
            item.setPhone(admin.getPhone()); // 使用真实的phone字段
            item.setAvatar(admin.getAvatar());
            item.setShopName(admin.getNickname() != null ? admin.getNickname() : admin.getUsername());
            // 查询商家的商品数量
            Integer productCount = adminMapper.countProductsByMerchantId(admin.getId());
            item.setProductCount(productCount != null ? productCount : 0);
            item.setStatus(admin.getStatus());
            item.setCreatedAt(admin.getCreatedAt());
            item.setLastLoginAt(admin.getLastLoginAt());
            return item;
        }).collect(Collectors.toList());

        AdminListVO vo = new AdminListVO();
        vo.setTotal(total);
        vo.setList(items);

        return vo;
    }

    @Override
    public Admin getProfile(Long id) {
        Admin admin = adminMapper.findById(id);
        if (admin == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 不返回密码
        admin.setPassword(null);
        return admin;
    }

    @Override
    @Transactional
    public void updateProfile(AdminUpdateProfileRequest request) {
        Admin admin = adminMapper.findById(request.getId());
        if (admin == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查邮箱是否被其他用户使用
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Admin existEmail = adminMapper.findByEmail(request.getEmail());
            if (existEmail != null && !existEmail.getId().equals(request.getId())) {
                throw new IllegalArgumentException("邮箱已被其他用户使用");
            }
        }

        // 更新信息
        if (request.getNickname() != null) {
            admin.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            admin.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            admin.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            admin.setAvatar(request.getAvatar());
        }

        adminMapper.updateProfile(admin);
    }

    @Override
    @Transactional
    public void changePassword(AdminChangePasswordRequest request) {
        Admin admin = adminMapper.findById(request.getId());
        if (admin == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        // 更新密码
        String newPasswordEncoded = passwordEncoder.encode(request.getNewPassword());
        adminMapper.updatePassword(request.getId(), newPasswordEncoded);
    }

    @Override
    @Transactional
    public void toggleMerchantStatus(Long id, Integer status) {
        Admin admin = adminMapper.findById(id);
        if (admin == null) {
            throw new IllegalArgumentException("商家不存在");
        }

        if (admin.getRole() != 1) {
            throw new IllegalArgumentException("只能操作商家账号");
        }

        // 更新状态
        adminMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void auditProduct(ProductAuditRequest request, Long adminId) {
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }

        if (request.getAuditStatus() == null || (request.getAuditStatus() != 1 && request.getAuditStatus() != 3)) {
            throw new IllegalArgumentException("审核状态无效");
        }

        // 查询商品（不限制状态）
        Product product = productMapper.selectByIdWithoutStatus(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        if (product.getStatus() != 2) {
            throw new IllegalArgumentException("只能审核待审核状态的商品");
        }

        // 查询审核人信息
        Admin auditor = adminMapper.findById(adminId);
        if (auditor == null) {
            throw new IllegalArgumentException("审核人不存在");
        }

        // 更新商品状态
        productMapper.updateProductStatus(request.getProductId(), request.getAuditStatus());

        // 创建审核记录
        ProductAudit audit = new ProductAudit();
        audit.setProductId(request.getProductId());
        audit.setMerchantId(product.getMerchantId());
        audit.setAuditStatus(request.getAuditStatus());
        audit.setAuditReason(request.getAuditReason());
        audit.setAuditorId(adminId);
        audit.setAuditorName(auditor.getNickname() != null ? auditor.getNickname() : auditor.getUsername());
        audit.setAuditTime(LocalDateTime.now());

        productAuditMapper.insert(audit);
    }

    @Override
    public Map<String, Object> getPendingProducts(Integer page, Integer size) {
        int offset = (page - 1) * size;
        
        // 查询待审核商品列表（status=2）
        List<Product> products = productMapper.selectMerchantProducts(
            null, null, null, 2, null, null, offset, size
        );
        
        // 统计总数
        int total = productMapper.countMerchantProducts(null, null, null, 2);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", products);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return result;
    }

    /**
     * 获取待审核商品列表（支持筛选）
     * @param productId 商品ID
     * @param productName 商品名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页数量
     * @return 待审核商品列表和分页信息
     */
    @Override
    public Map<String, Object> getPendingProductsWithFilter(Long productId, String productName, 
                                                             String startTime, String endTime,
                                                             Integer page, Integer size) {
        int offset = (page - 1) * size;
        
        // 查询待审核商品列表（status=2，支持筛选）
        List<Product> products = productMapper.selectPendingProductsWithFilter(
            productId, productName, startTime, endTime, offset, size
        );
        
        // 统计总数
        int total = productMapper.countPendingProductsWithFilter(productId, productName, startTime, endTime);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", products);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return result;
    }

    /**
     * 获取商品详情（管理员用于审核，不限制商家权限）
     * @param productId 商品ID
     * @return 商品详情
     */
    @Override
    public com.example.VO.ProductDetailVO getProductDetailForAudit(Long productId) {
        // 使用不限制状态的查询方法
        Product product = productMapper.selectByIdWithoutStatus(productId);
        
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        
        // 转换为 VO
        com.example.VO.ProductDetailVO vo = new com.example.VO.ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setMainImage(product.getMainImage());
        vo.setImage(product.getMainImage());
        vo.setCategoryId(product.getCategoryId());
        vo.setMerchantId(product.getMerchantId());
        vo.setStock(product.getStock());
        vo.setSoldCount(product.getSoldCount());
        vo.setUnit(product.getUnit());
        vo.setWeight(product.getWeight());
        vo.setShelfLife(product.getShelfLife());
        vo.setOriginPlace(product.getOriginPlace());
        vo.setBrand(product.getBrand());
        vo.setDeliveryType(product.getDeliveryType());
        vo.setIsRecommended(product.getIsRecommended());
        vo.setIsHot(product.getIsHot());
        vo.setIsNew(product.getIsNew());
        vo.setStatus(product.getStatus());
        
        return vo;
    }

    /**
     * 获取管理员的审核记录
     * @param auditorId 审核人ID
     * @param page 页码
     * @param size 每页数量
     * @return 审核记录列表和分页信息
     */
    @Override
    public Map<String, Object> getAdminAuditRecords(Long auditorId, Integer page, Integer size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 查询审核记录列表
        List<ProductAuditVO> records = productAuditMapper.selectByAuditorId(auditorId, offset, size);
        
        // 统计总数
        int total = productAuditMapper.countByAuditorId(auditorId);
        
        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return result;
    }
}
