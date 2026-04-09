package com.example.Mapper;

import com.example.Entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    // 查询所有分类
    List<Category> selectAll();
    
    // 查询所有启用的分类（小程序端）
    List<Category> selectEnabled();
    
    // 根据ID查询分类
    Category selectById(@Param("id") Long id);
    
    // 根据名称查询分类
    Category selectByName(@Param("name") String name);
    
    // 插入分类
    int insert(Category category);
    
    // 更新分类
    int update(Category category);
    
    // 删除分类
    int deleteById(@Param("id") Long id);
    
    // 更新分类状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    // 更新商品数量
    int updateProductCount(@Param("id") Long id, @Param("count") Integer count);
    
    // 查询分类下的商品数量
    int countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
