package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.merchant.DeliverOrderRequest;
import com.example.DTO.merchant.MerchantOrderListRequest;
import com.example.DTO.merchant.MerchantProductListRequest;
import com.example.Service.OrderService;
import com.example.Service.ProductService;
import com.example.VO.DashboardStatsVO;
import com.example.VO.DashboardTodosVO;
import com.example.VO.HotProductVO;
import com.example.VO.MerchantOrderVO;
import com.example.VO.ProductDetailVO;
import com.example.VO.RecentOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;

    @Value("D:/my-images/product/")
    private String productImageLocalPath;

    @Value("${app.image.product-path}")
    private String productImageWebPath;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * 上传商品图片
     */
    @PostMapping("/products/upload-image")
    public ApiResponse<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 确保目录存在
            File dir = new File(productImageLocalPath);
            if (!dir.exists()) dir.mkdirs();

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + ext;
            String filePath = productImageLocalPath + fileName;

            // 3. 保存文件
            file.transferTo(new File(filePath));

            // 4. 构建完整的 URL（拼接 baseUrl + webPath + fileName）
            String url = baseUrl + productImageWebPath + fileName;
            return ApiResponse.success(url);
        } catch (IOException e) {
            return ApiResponse.error("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取商家商品列表
     */
    @GetMapping("/products")
    public ApiResponse<Map<String, Object>> getMerchantProducts(
            @RequestParam Long merchantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        try {
            MerchantProductListRequest request = new MerchantProductListRequest();
            request.setMerchantId(merchantId);
            request.setName(name);
            request.setCategoryId(categoryId);
            request.setStatus(status);
            request.setStartTime(startTime);
            request.setEndTime(endTime);
            request.setSortField(sortField);
            request.setSortOrder(sortOrder);
            request.setPage(page);
            request.setSize(size);

            Map<String, Object> result = productService.getMerchantProducts(request);
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取商品列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取商品详情（用于编辑）
     */
    @GetMapping("/products/{id}")
    public ApiResponse<ProductDetailVO> getProductDetail(
            @PathVariable Long id,
            @RequestParam Long merchantId) {
        try {
            ProductDetailVO detail = productService.getMerchantProductDetail(id, merchantId);
            return ApiResponse.success("获取商品信息成功",detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取商品详情失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping("/products")
    public ApiResponse<Void> updateProduct(
            @RequestBody com.example.DTO.product.ProductUpdateRequest request,
            @RequestParam Long merchantId) {
        try {
            productService.updateProduct(request, merchantId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新商品失败：" + e.getMessage());
        }
    }

    /**
     * 添加商品（待审核状态）
     */
    @PostMapping("/products")
    public ApiResponse<Void> createProduct(
            @RequestBody com.example.DTO.product.ProductUpdateRequest request,
            @RequestParam Long merchantId) {
        try {
            productService.createProduct(request, merchantId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("添加商品失败：" + e.getMessage());
        }
    }

    /**
     * 获取商家的审核记录
     */
    @GetMapping("/audit-records")
    public ApiResponse<Map<String, Object>> getAuditRecords(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> result = productService.getMerchantAuditRecords(merchantId, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取审核记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商家订单列表
     */
    @GetMapping("/orders")
    public ApiResponse<Map<String, Object>> getMerchantOrders(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            MerchantOrderListRequest request = new MerchantOrderListRequest();
            request.setMerchantId(merchantId);
            request.setOrderStatus(orderStatus);
            request.setOrderNo(orderNo);
            request.setPhone(phone);
            request.setStartTime(startTime);
            request.setEndTime(endTime);
            request.setPage(page);
            request.setSize(size);
            
            Map<String, Object> result = orderService.getMerchantOrders(request);
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商家订单详情
     */
    @GetMapping("/orders/{orderNo}")
    public ApiResponse<MerchantOrderVO> getMerchantOrderDetail(
            @PathVariable String orderNo,
            @RequestParam Long merchantId) {
        try {
            MerchantOrderVO detail = orderService.getMerchantOrderDetail(orderNo, merchantId);
            return ApiResponse.success(detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取订单详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 商家发货
     */
    @PutMapping("/orders/{orderNo}/deliver")
    public ApiResponse<Void> deliverOrder(
            @PathVariable String orderNo,
            @RequestParam Long merchantId,
            @RequestBody DeliverOrderRequest request) {
        try {
            orderService.deliverOrder(orderNo, merchantId, request.getDeliveryCompany(), request.getDeliveryNo());
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("发货失败：" + e.getMessage());
        }
    }
    
    // ==================== Dashboard 相关接口 ====================
    
    /**
     * 获取商家 Dashboard 统计数据
     */
    @GetMapping("/dashboard/stats")
    public ApiResponse<DashboardStatsVO> getDashboardStats(@RequestParam Long merchantId) {
        try {
            DashboardStatsVO stats = orderService.getMerchantDashboardStats(merchantId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取待办事项
     */
    @GetMapping("/dashboard/todos")
    public ApiResponse<DashboardTodosVO> getDashboardTodos(@RequestParam Long merchantId) {
        try {
            DashboardTodosVO todos = orderService.getMerchantDashboardTodos(merchantId);
            return ApiResponse.success(todos);
        } catch (Exception e) {
            return ApiResponse.error("获取待办事项失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取热销商品TOP N
     */
    @GetMapping("/dashboard/hot-products")
    public ApiResponse<List<HotProductVO>> getHotProducts(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<HotProductVO> products = productService.getHotProducts(merchantId, limit);
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error("获取热销商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取最近订单
     */
    @GetMapping("/dashboard/recent-orders")
    public ApiResponse<List<RecentOrderVO>> getRecentOrders(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<RecentOrderVO> orders = orderService.getRecentOrders(merchantId, limit);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error("获取最近订单失败：" + e.getMessage());
        }
    }
}
