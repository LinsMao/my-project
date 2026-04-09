package com.example.Mapper;

import com.example.Entity.ProductAudit;
import com.example.VO.ProductAuditVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductAuditMapper {
    
    // 插入审核记录
    int insert(ProductAudit audit);
    
    // 根据商品ID查询审核记录列表
    List<ProductAuditVO> selectByProductId(@Param("productId") Long productId);
    
    // 根据商家ID查询审核记录列表
    List<ProductAuditVO> selectByMerchantId(@Param("merchantId") Long merchantId,
                                            @Param("offset") int offset,
                                            @Param("size") int size);
    
    // 统计商家审核记录总数
    int countByMerchantId(@Param("merchantId") Long merchantId);

    // 根据审核人ID查询审核记录列表
    List<ProductAuditVO> selectByAuditorId(@Param("auditorId") Long auditorId,
                                           @Param("offset") int offset,
                                           @Param("size") int size);
    
    // 统计审核人审核记录总数
    int countByAuditorId(@Param("auditorId") Long auditorId);
}