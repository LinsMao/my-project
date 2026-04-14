package com.example.Mapper;

import com.example.Entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    //商品
    List<Product> selectHomeProductPage( @Param("offset") int offset,
                                         @Param("size") int size);

    // 根据分类ID查询商品
    List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);

    // 根据ID查询商品详情
    Product selectById(@Param("id") Long id);

    // 根据ID查询商品详情（不限制状态，用于审核等场景）
    Product selectByIdWithoutStatus(@Param("id") Long id);

    // 更新商品库存
    int updateStock(Product product);

    // 更新商品销量
    int updateSoldCount(@Param("id") Long id, @Param("soldCount") Integer soldCount);

    // 增加商品浏览量
    int incrementViewCount(@Param("id") Long id);

    // 搜索商品（根据关键词）
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // 查询商家商品列表
    List<Product> selectMerchantProducts(@Param("merchantId") Long merchantId,
                                         @Param("name") String name,
                                         @Param("categoryId") Integer categoryId,
                                         @Param("status") Integer status,
                                         @Param("startTime") String startTime,
                                         @Param("endTime") String endTime,
                                         @Param("sortField") String sortField,
                                         @Param("sortOrder") String sortOrder,
                                         @Param("offset") int offset,
                                         @Param("size") int size);

    // 查询商家商品总数
    int countMerchantProducts(@Param("merchantId") Long merchantId,
                              @Param("name") String name,
                              @Param("categoryId") Integer categoryId,
                              @Param("status") Integer status,
                              @Param("startTime") String startTime,
                              @Param("endTime") String endTime);

    // 根据ID和商家ID查询商品（用于权限验证）
    Product selectByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);

    // 更新商品
    int updateProduct(Product product);

    // 更新商品状态
    int updateProductStatus(@Param("id") Long id, @Param("status") Integer status);

    // 添加商品
    int insertProduct(Product product);

    // 查询待审核商品列表（支持筛选）
    List<Product> selectPendingProductsWithFilter(@Param("productId") Long productId,
                                                   @Param("productName") String productName,
                                                   @Param("startTime") String startTime,
                                                   @Param("endTime") String endTime,
                                                   @Param("offset") int offset,
                                                   @Param("size") int size);

    // 统计待审核商品总数（支持筛选）
    int countPendingProductsWithFilter(@Param("productId") Long productId,
                                        @Param("productName") String productName,
                                        @Param("startTime") String startTime,
                                        @Param("endTime") String endTime);
    
    // 查询商家热销商品
    List<Product> selectHotProducts(@Param("merchantId") Long merchantId, @Param("limit") Integer limit);

    // 统计库存不足的商品数量
    Integer countLowStockProducts(@Param("merchantId") Long merchantId);
    
    // 平台统计方法
    Integer countPlatformProducts();
    Integer countPlatformOnSaleProducts();
    List<Product> selectPlatformHotProducts(@Param("limit") Integer limit);
    
    // 管理员商品管理
    List<Product> selectAdminProducts(@Param("productName") String productName,
                                      @Param("merchantName") String merchantName,
                                      @Param("categoryId") Integer categoryId,
                                      @Param("status") Integer status,
                                      @Param("startTime") String startTime,
                                      @Param("endTime") String endTime,
                                      @Param("sortField") String sortField,
                                      @Param("sortOrder") String sortOrder,
                                      @Param("offset") int offset,
                                      @Param("size") int size);
    
    int countAdminProducts(@Param("productName") String productName,
                          @Param("merchantName") String merchantName,
                          @Param("categoryId") Integer categoryId,
                          @Param("status") Integer status,
                          @Param("startTime") String startTime,
                          @Param("endTime") String endTime);
    
    // 商品统计
    Integer countProductsByStatus(@Param("status") Integer status);
}
