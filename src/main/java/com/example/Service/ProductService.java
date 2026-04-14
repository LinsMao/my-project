package com.example.Service;

import com.example.DTO.merchant.MerchantProductListRequest;
import com.example.DTO.product.ProductUpdateRequest;
import com.example.VO.HotProductVO;
import com.example.VO.MerchantProductVO;
import com.example.VO.ProductDetailVO;
import com.example.VO.ProductVO;

import java.util.List;
import java.util.Map;

public interface ProductService {

    /**
     * 获取首页商品列表
     */
    List<ProductVO> getHomeProductPage(int page, int size);

    /**
     * 根据分类ID获取商品列表
     */
    List<ProductVO> getProductsByCategory(Long categoryId);

    /**
     * 获取商品详情信息
     */
    ProductDetailVO getProductDetail(Long id, Long userId);

    /**
     * 搜索商品
     */
    List<ProductVO> searchProducts(String keyword);

    /**
     * 获取商家商品列表
     */
    Map<String, Object> getMerchantProducts(MerchantProductListRequest request);

    /**
     * 获取商家商品详情（用于编辑）
     */
    ProductDetailVO getMerchantProductDetail(Long productId, Long merchantId);

    /**
     * 更新商品
     */
    void updateProduct(ProductUpdateRequest request, Long merchantId);

    /**
     * 添加商品（待审核状态）
     */
    void createProduct(ProductUpdateRequest request, Long merchantId);

    /**
     * 获取商家的审核记录
     */
    Map<String, Object> getMerchantAuditRecords(Long merchantId, Integer page, Integer size);

    /**
     * 获取管理员的审核记录
     * @param auditorId 审核人ID
     * @param page 页码
     * @param size 每页数量
     * @return 审核记录列表和分页信息
     */
    Map<String, Object> getAdminAuditRecords(Long auditorId, Integer page, Integer size);
    
    /**
     * 获取热销商品TOP N
     */
    List<HotProductVO> getHotProducts(Long merchantId, Integer limit);
}
