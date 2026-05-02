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

        // 更新商家状态
        adminMapper.updateStatus(id, status);
        
        // 如果是禁用商家（status = 0），则自动下架该商家的所有商品
        if (status == 0) {
            productMapper.updateProductStatusByMerchantId(id, 0);
        }
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
            null, null, null, 2, null, null, null, null, offset, size
        );
        
        // 统计总数
        int total = productMapper.countMerchantProducts(null, null, null, 2, null, null);
        
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

    @Autowired
    private com.example.Mapper.OrderMapper orderMapper;

    @Override
    public com.example.VO.AdminDashboardStatsVO getAdminDashboardStats() {
        com.example.VO.AdminDashboardStatsVO stats = new com.example.VO.AdminDashboardStatsVO();
        
        // 今日订单数
        Integer todayOrders = orderMapper.countPlatformTodayOrders();
        stats.setTodayOrders(todayOrders != null ? todayOrders : 0);
        
        // 昨日订单数
        Integer yesterdayOrders = orderMapper.countPlatformYesterdayOrders();
        
        // 计算增长率
        if (yesterdayOrders != null && yesterdayOrders > 0) {
            double growth = ((todayOrders - yesterdayOrders) * 100.0) / yesterdayOrders;
            stats.setTodayOrdersGrowth(Math.round(growth * 10) / 10.0);
        } else {
            stats.setTodayOrdersGrowth(0.0);
        }
        
        // 今日销售额
        Double todaySales = orderMapper.sumPlatformTodaySales();
        stats.setTodaySales(todaySales != null ? todaySales : 0.0);
        
        // 昨日销售额
        Double yesterdaySales = orderMapper.sumPlatformYesterdaySales();
        
        // 计算销售额增长率
        if (yesterdaySales != null && yesterdaySales > 0) {
            double growth = ((todaySales - yesterdaySales) * 100.0) / yesterdaySales;
            stats.setTodaySalesGrowth(Math.round(growth * 10) / 10.0);
        } else {
            stats.setTodaySalesGrowth(0.0);
        }
        
        // 商家总数
        Integer totalMerchants = adminMapper.countTotalMerchants();
        stats.setTotalMerchants(totalMerchants != null ? totalMerchants : 0);
        
        // 用户总数
        Integer totalUsers = adminMapper.countTotalUsers();
        stats.setTotalUsers(totalUsers != null ? totalUsers : 0);
        
        // 商品总数
        Integer totalProducts = productMapper.countPlatformProducts();
        stats.setTotalProducts(totalProducts != null ? totalProducts : 0);
        
        // 在售商品数
        Integer onSaleProducts = productMapper.countPlatformOnSaleProducts();
        stats.setOnSaleProducts(onSaleProducts != null ? onSaleProducts : 0);
        
        return stats;
    }
    
    @Override
    public com.example.VO.AdminDashboardTodosVO getAdminDashboardTodos() {
        com.example.VO.AdminDashboardTodosVO todos = new com.example.VO.AdminDashboardTodosVO();
        
        // 待审核商品
        Integer pendingAudit = productMapper.countPendingProductsWithFilter(null, null, null, null);
        todos.setPendingAuditProducts(pendingAudit != null ? pendingAudit : 0);
        
        // 待处理订单（待发货）
        Integer pendingOrders = orderMapper.countPlatformPendingOrders();
        todos.setPendingOrders(pendingOrders != null ? pendingOrders : 0);
        
        return todos;
    }
    
    @Override
    public java.util.List<com.example.VO.HotProductVO> getPlatformHotProducts(Integer limit) {
        List<Product> products = productMapper.selectPlatformHotProducts(limit);
        
        java.util.List<com.example.VO.HotProductVO> result = new java.util.ArrayList<>();
        for (Product p : products) {
            com.example.VO.HotProductVO vo = new com.example.VO.HotProductVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setMainImage(p.getMainImage());
            vo.setSoldCount(p.getSoldCount());
            vo.setPrice(p.getPrice());
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    public java.util.List<com.example.VO.RecentMerchantVO> getRecentMerchants(Integer limit) {
        List<Admin> merchants = adminMapper.selectRecentMerchants(limit);
        
        java.util.List<com.example.VO.RecentMerchantVO> result = new java.util.ArrayList<>();
        for (Admin merchant : merchants) {
            com.example.VO.RecentMerchantVO vo = new com.example.VO.RecentMerchantVO();
            vo.setId(merchant.getId());
            vo.setUsername(merchant.getUsername());
            vo.setNickname(merchant.getNickname());
            vo.setPhone(merchant.getPhone());
            vo.setCreateTime(merchant.getCreatedAt());
            result.add(vo);
        }
        
        return result;
    }

    // ==================== 管理员商品管理 ====================
    
    @Override
    public Map<String, Object> getAdminProductList(com.example.DTO.admin.AdminProductListRequest request) {
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 查询商品列表
        List<Product> products = productMapper.selectAdminProducts(
            request.getProductName(),
            request.getMerchantName(),
            request.getCategoryId(),
            request.getStatus(),
            request.getStartTime(),
            request.getEndTime(),
            request.getSortField(),
            request.getSortOrder(),
            offset,
            request.getSize()
        );
        
        // 转换为VO
        List<com.example.VO.AdminProductVO> productVOs = products.stream().map(product -> {
            com.example.VO.AdminProductVO vo = new com.example.VO.AdminProductVO();
            vo.setId(product.getId());
            vo.setName(product.getName());
            vo.setMainImage(product.getMainImage());
            vo.setCategoryId(product.getCategoryId());
            vo.setMerchantId(product.getMerchantId());
            vo.setPrice(product.getPrice());
            vo.setOriginalPrice(product.getOriginalPrice());
            vo.setStock(product.getStock());
            vo.setSoldCount(product.getSoldCount());
            vo.setViewCount(product.getViewCount());
            vo.setStatus(product.getStatus());
            vo.setCreateTime(product.getCreateTime());
            vo.setUpdateTime(product.getUpdateTime());
            
            // 获取分类名称
            String categoryName = getCategoryName(product.getCategoryId());
            vo.setCategoryName(categoryName);
            
            // 获取商家名称
            Admin merchant = adminMapper.findById(product.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getUsername());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        // 查询总数
        int total = productMapper.countAdminProducts(
            request.getProductName(),
            request.getMerchantName(),
            request.getCategoryId(),
            request.getStatus(),
            request.getStartTime(),
            request.getEndTime()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", productVOs);
        result.put("total", total);
        return result;
    }
    
    @Override
    public com.example.VO.AdminProductStatisticsVO getProductStatistics() {
        com.example.VO.AdminProductStatisticsVO stats = new com.example.VO.AdminProductStatisticsVO();
        
        // 总商品数
        Integer totalProducts = productMapper.countPlatformProducts();
        stats.setTotalProducts(totalProducts != null ? totalProducts : 0);
        
        // 在售商品数
        Integer onSaleProducts = productMapper.countProductsByStatus(1);
        stats.setOnSaleProducts(onSaleProducts != null ? onSaleProducts : 0);
        
        // 下架商品数
        Integer offSaleProducts = productMapper.countProductsByStatus(0);
        stats.setOffSaleProducts(offSaleProducts != null ? offSaleProducts : 0);
        
        // 待审核商品数
        Integer pendingProducts = productMapper.countProductsByStatus(2);
        stats.setPendingProducts(pendingProducts != null ? pendingProducts : 0);
        
        return stats;
    }
    
    @Override
    @Transactional
    public void forceOfflineProduct(Long productId, String reason) {
        // 更新商品状态为下架
        productMapper.updateProductStatus(productId, 0);
        
        // 记录审核记录（作为强制下架记录）
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setAuditStatus(0); // 0表示强制下架
        audit.setAuditReason(reason);
        audit.setAuditTime(LocalDateTime.now());
        productAuditMapper.insert(audit);
    }
    
    private String getCategoryName(Integer categoryId) {
        if (categoryId == null) return "未分类";
        Map<Integer, String> categoryMap = new HashMap<>();
        categoryMap.put(1, "水果");
        categoryMap.put(2, "蔬菜");
        categoryMap.put(3, "肉类");
        categoryMap.put(4, "海鲜");
        categoryMap.put(5, "粮油");
        categoryMap.put(6, "乳品");
        return categoryMap.getOrDefault(categoryId, "未分类");
    }

    // ==================== 管理员订单管理 ====================
    
    @Autowired
    private com.example.Mapper.OrderItemMapper orderItemMapper;
    
    @Override
    public Map<String, Object> getAdminOrderList(com.example.DTO.admin.AdminOrderListRequest request) {
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 查询订单列表
        List<com.example.Entity.Orders> orders = orderMapper.selectAdminOrders(
            request.getOrderNo(),
            request.getMerchantName(),
            request.getUserPhone(),
            request.getOrderStatus(),
            request.getStartTime(),
            request.getEndTime(),
            offset,
            request.getSize()
        );
        
        // 转换为VO
        List<com.example.VO.AdminOrderVO> orderVOs = orders.stream().map(order -> {
            com.example.VO.AdminOrderVO vo = new com.example.VO.AdminOrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            vo.setMerchantId(order.getMerchantId());
            
            // 获取商家名称
            Admin merchant = adminMapper.findById(order.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getUsername());
            }
            
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPayAmount(order.getPayAmount());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setPaymentMethod(order.getPayType());
            vo.setReceiverName(order.getReceiverName());
            vo.setReceiverPhone(order.getReceiverPhone());
            vo.setReceiverAddress(order.getReceiverAddress());
            vo.setRemark(order.getRemark());
            vo.setDeliveryCompany(order.getDeliveryCompany());
            vo.setDeliveryNo(order.getDeliveryNo());
            vo.setCreateTime(order.getCreateTime());
            vo.setPayTime(order.getPayTime());
            vo.setDeliveryTime(order.getDeliveryTime());
            vo.setFinishTime(order.getReceiveTime());
            
            // 查询订单项
            List<com.example.Entity.OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
            List<com.example.VO.AdminOrderItemVO> itemVOs = orderItems.stream().map(item -> {
                com.example.VO.AdminOrderItemVO itemVO = new com.example.VO.AdminOrderItemVO();
                itemVO.setId(item.getId());
                itemVO.setProductId(item.getProductId());
                itemVO.setProductName(item.getProductName());
                itemVO.setProductImage(item.getProductImage());
                itemVO.setPrice(item.getPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setUnit(item.getUnit());
                itemVO.setTotalAmount(item.getTotalAmount());
                return itemVO;
            }).collect(Collectors.toList());
            vo.setItems(itemVOs);
            
            return vo;
        }).collect(Collectors.toList());
        
        // 查询总数
        int total = orderMapper.countAdminOrders(
            request.getOrderNo(),
            request.getMerchantName(),
            request.getUserPhone(),
            request.getOrderStatus(),
            request.getStartTime(),
            request.getEndTime()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderVOs);
        result.put("total", total);
        return result;
    }
    
    @Override
    public com.example.VO.AdminOrderVO getAdminOrderDetail(String orderNo) {
        // 查询订单（不限制商家）
        com.example.Entity.Orders order = orderMapper.selectByOrderNoForAdmin(orderNo);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 转换为VO
        com.example.VO.AdminOrderVO vo = new com.example.VO.AdminOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setMerchantId(order.getMerchantId());
        
        // 获取商家名称
        Admin merchant = adminMapper.findById(order.getMerchantId());
        if (merchant != null) {
            vo.setMerchantName(merchant.getUsername());
        }
        
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setPaymentMethod(order.getPayType());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setDeliveryCompany(order.getDeliveryCompany());
        vo.setDeliveryNo(order.getDeliveryNo());
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setFinishTime(order.getReceiveTime());
        
        // 查询订单项
        List<com.example.Entity.OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        List<com.example.VO.AdminOrderItemVO> itemVOs = orderItems.stream().map(item -> {
            com.example.VO.AdminOrderItemVO itemVO = new com.example.VO.AdminOrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImage(item.getProductImage());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setUnit(item.getUnit());
            itemVO.setTotalAmount(item.getTotalAmount());
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);
        
        return vo;
    }
}
