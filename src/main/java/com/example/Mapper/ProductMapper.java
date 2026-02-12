package com.example.Mapper;

import com.example.Entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> selectHomeProductPage( @Param("offset") int offset,
                                         @Param("size") int size);
}
