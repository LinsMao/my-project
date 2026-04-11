package com.example.Service.impl;

import com.example.DTO.merchant.MerchantProductListRequest;
import com.example.DTO.product.ProductUpdateRequest;
import com.example.Entity.Admin;
import com.example.Entity.Product;
import com.example.Mapper.ProductAuditMapper;
import com.example.Mapper.ProductMapper;
import com.example.Mapper.admin.AdminMapper;
import com.example.Service.ProductService;
import com.example.VO.MerchantProductVO;
import com.example.VO.ProductAuditVO;
import com.example.VO.ProductDetailVO;
import com.example.VO.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductAuditMapper productAuditMapper;
    
    @Autowired
    private AdminMapper adminMapper;



    @Override
    public List<ProductVO> getHomeProductPage(int page, int size) {

        if (page < 1) page = 1;
        if (size <= 0) size = 10;

        int offset = (page - 1) * size;

        List<Product> products =
                productMapper.selectHomeProductPage(offset, size);

        List<ProductVO> result = new ArrayList<>();

        for (Product p : products) {
            ProductVO vo = new ProductVO();

            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setSubtitle(p.getSubtitle());
            vo.setPrice(p.getPrice());
            vo.setOriginalPrice(p.getOriginalPrice());
            vo.setSoldCount(p.getSoldCount());
            vo.setUnit(p.getUnit());

            // 主图：不拼接 URL
            vo.setImage(p.getMainImage());

            // 首页标签
            List<String> tags = new ArrayList<>();
            if (p.getIsRecommended() != null && p.getIsRecommended() == 1) {
                tags.add("推荐");
            }
            if (p.getIsHot() != null && p.getIsHot() == 1) {
                tags.add("热销");
            }
            if (p.getIsNew() != null && p.getIsNew() == 1) {
                tags.add("新品");
            }
            vo.setTagList(tags);

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<ProductVO> getProductsByCategory(Long categoryId) {
        List<Product> products = productMapper.selectByCategoryId(categoryId);
        List<ProductVO> result = new ArrayList<>();

        for (Product p : products) {
            ProductVO vo = new ProductVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setSubtitle(p.getSubtitle());
            vo.setPrice(p.getPrice());
            vo.setOriginalPrice(p.getOriginalPrice());
            vo.setSoldCount(p.getSoldCount());
            vo.setUnit(p.getUnit());
            
            // 处理图片URL：确保返回完整URL
            String imageUrl = p.getMainImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    imageUrl = "http://192.168.1.26:8080" + imageUrl;
                }
            }
            vo.setImage(imageUrl);

            // 标签
            List<String> tags = new ArrayList<>();
            if (p.getIsRecommended() != null && p.getIsRecommended() == 1) {
                tags.add("推荐");
            }
            if (p.getIsHot() != null && p.getIsHot() == 1) {
                tags.add("热销");
            }
            if (p.getIsNew() != null && p.getIsNew() == 1) {
                tags.add("新品");
            }
            vo.setTagList(tags);

            result.add(vo);
        }

        return result;
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {

        if(id==null||id==0){
            throw new IllegalArgumentException("商品ID不合法");
        }

        Product product = productMapper.selectById(id);
        if (product == null){
            throw new RuntimeException("商品不存在或已下架");
        }

        // 组装 VO
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        
        // 处理图片URL：确保返回完整URL
        String imageUrl = product.getMainImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                imageUrl = "http://192.168.1.26:8080" + imageUrl;
            }
        }
        vo.setMainImage(imageUrl);
        vo.setImage(imageUrl);  // 小程序使用
        
        vo.setSoldCount(product.getSoldCount());
        vo.setStock(product.getStock());
        vo.setUnit(product.getUnit());
        vo.setOriginPlace(product.getOriginPlace());
        vo.setBrand(product.getBrand());
        vo.setShelfLife(product.getShelfLife());
        vo.setDeliveryType(product.getDeliveryType());
        vo.setIsRecommended(product.getIsRecommended());
        vo.setIsHot(product.getIsHot());
        vo.setIsNew(product.getIsNew());
        vo.setStatus(product.getStatus());
        vo.setCategoryId(product.getCategoryId());
        vo.setMerchantId(product.getMerchantId());
        
        // 查询商家名称
        if (product.getMerchantId() != null) {
            Admin merchant = adminMapper.findById(product.getMerchantId());
            if (merchant != null && merchant.getRole() == 1) {
                vo.setMerchantName(merchant.getNickname());
            }
        }
        
        // 生成标签列表
        List<String> tags = new ArrayList<>();
        if (product.getIsRecommended() != null && product.getIsRecommended() == 1) {
            tags.add("推荐");
        }
        if (product.getIsHot() != null && product.getIsHot() == 1) {
            tags.add("热销");
        }
        if (product.getIsNew() != null && product.getIsNew() == 1) {
            tags.add("新品");
        }
        vo.setTagList(tags);

        return vo;
    }

    @Override
    public List<ProductVO> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 调用Mapper搜索
        List<Product> products = productMapper.searchByKeyword(keyword.trim());

        // 转换为VO
        List<ProductVO> result = new ArrayList<>();
        for (Product p : products) {
            ProductVO vo = new ProductVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setSubtitle(p.getSubtitle());
            vo.setPrice(p.getPrice());
            vo.setOriginalPrice(p.getOriginalPrice());
            vo.setSoldCount(p.getSoldCount());
            vo.setUnit(p.getUnit());
            vo.setImage(p.getMainImage());

            // 标签
            List<String> tags = new ArrayList<>();
            if (p.getIsRecommended() != null && p.getIsRecommended() == 1) {
                tags.add("推荐");
            }
            if (p.getIsHot() != null && p.getIsHot() == 1) {
                tags.add("热销");
            }
            if (p.getIsNew() != null && p.getIsNew() == 1) {
                tags.add("新品");
            }
            vo.setTagList(tags);

            result.add(vo);
        }

        return result;
    }

    @Override
    public Map<String, Object> getMerchantProducts(MerchantProductListRequest request) {
        if (request.getMerchantId() == null) {
            throw new IllegalArgumentException("商家ID不能为空");
        }

        if (request.getPage() < 1) request.setPage(1);
        if (request.getSize() <= 0) request.setSize(12);

        int offset = (request.getPage() - 1) * request.getSize();

        // 查询商品列表
        List<Product> products = productMapper.selectMerchantProducts(
                request.getMerchantId(),
                request.getName(),
                request.getCategoryId(),
                request.getStatus(),
                request.getSortField(),
                request.getSortOrder(),
                offset,
                request.getSize()
        );

        // 查询总数
        int total = productMapper.countMerchantProducts(
                request.getMerchantId(),
                request.getName(),
                request.getCategoryId(),
                request.getStatus()
        );

        // 分类映射
        Map<Integer, String> categoryMap = new HashMap<>();
        categoryMap.put(1, "水果");
        categoryMap.put(2, "蔬菜");
        categoryMap.put(3, "肉类");
        categoryMap.put(4, "海鲜");

        // 转换为VO
        List<MerchantProductVO> voList = new ArrayList<>();
        for (Product p : products) {
            MerchantProductVO vo = new MerchantProductVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setMainImage(p.getMainImage());
            vo.setCategoryId(p.getCategoryId());
            vo.setCategoryName(categoryMap.getOrDefault(p.getCategoryId(), "未知"));
            vo.setPrice(p.getPrice());
            vo.setOriginalPrice(p.getOriginalPrice());
            vo.setStock(p.getStock());
            vo.setSoldCount(p.getSoldCount());
            vo.setStatus(p.getStatus());
            vo.setIsRecommended(p.getIsRecommended());
            vo.setCreateTime(p.getCreateTime());
            voList.add(vo);
        }

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", total);
        result.put("page", request.getPage());
        result.put("size", request.getSize());

        return result;
    }

    @Override
    public ProductDetailVO getMerchantProductDetail(Long productId, Long merchantId) {
        if (productId == null || merchantId == null) {
            throw new IllegalArgumentException("商品ID和商家ID不能为空");
        }

        // 查询商品并验证商家权限
        Product product = productMapper.selectByIdAndMerchantId(productId, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权限访问");
        }

        // 转换为VO
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setMainImage(product.getMainImage());
        vo.setImage(product.getMainImage());  // 小程序使用
        vo.setStock(product.getStock());
        vo.setUnit(product.getUnit());
        vo.setOriginPlace(product.getOriginPlace());
        vo.setBrand(product.getBrand());
        vo.setShelfLife(product.getShelfLife());
        vo.setCategoryId(product.getCategoryId());
        vo.setStatus(product.getStatus());
        vo.setIsRecommended(product.getIsRecommended());
        vo.setIsHot(product.getIsHot());
        vo.setIsNew(product.getIsNew());

        return vo;
    }

    @Override
    public void updateProduct(ProductUpdateRequest request, Long merchantId) {
        if (request.getId() == null || merchantId == null) {
            throw new IllegalArgumentException("商品ID和商家ID不能为空");
        }

        // 验证商家权限
        Product existingProduct = productMapper.selectByIdAndMerchantId(request.getId(), merchantId);
        if (existingProduct == null) {
            throw new RuntimeException("商品不存在或无权限修改");
        }

        // 更新商品
        Product product = new Product();
        product.setId(request.getId());
        product.setName(request.getName());
        product.setSubtitle(request.getSubtitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setMainImage(request.getMainImage());
        product.setStock(request.getStock());
        product.setUnit(request.getUnit());
        product.setOriginPlace(request.getOriginPlace());
        product.setBrand(request.getBrand());
        product.setShelfLife(request.getShelfLife());
        product.setDeliveryType(request.getDeliveryType());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(request.getStatus());
        product.setIsRecommended(request.getIsRecommended());
        product.setIsHot(request.getIsHot());
        product.setIsNew(request.getIsNew());

        int result = productMapper.updateProduct(product);
        if (result == 0) {
            throw new RuntimeException("更新商品失败");
        }
    }

    @Override
    public void createProduct(ProductUpdateRequest request, Long merchantId) {
        if (merchantId == null) {
            throw new IllegalArgumentException("商家ID不能为空");
        }

        // 创建商品
        Product product = new Product();
        product.setName(request.getName());
        product.setSubtitle(request.getSubtitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setMainImage(request.getMainImage());
        product.setStock(request.getStock());
        product.setUnit(request.getUnit());
        product.setOriginPlace(request.getOriginPlace());
        product.setBrand(request.getBrand());
        product.setShelfLife(request.getShelfLife());
        product.setDeliveryType(request.getDeliveryType());
        product.setCategoryId(request.getCategoryId());
        product.setMerchantId(merchantId);
        product.setStatus(2); // 待审核状态
        product.setIsRecommended(request.getIsRecommended());
        product.setIsHot(request.getIsHot());
        product.setIsNew(request.getIsNew());
        product.setSoldCount(0);
        product.setViewCount(0);

        int result = productMapper.insertProduct(product);
        if (result == 0) {
            throw new RuntimeException("添加商品失败");
        }
    }

    @Override
    public Map<String, Object> getMerchantAuditRecords(Long merchantId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        
        // 查询审核记录
        List<ProductAuditVO> records = productAuditMapper.selectByMerchantId(merchantId, offset, size);
        
        // 统计总数
        int total = productAuditMapper.countByMerchantId(merchantId);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return result;
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
