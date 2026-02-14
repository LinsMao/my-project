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


    // 根据ID查询商品详情
    Product selectById(@Param("id") Long id);
}
