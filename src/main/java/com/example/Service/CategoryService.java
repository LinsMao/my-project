package com.example.Service;

import com.example.DTO.category.CategoryRequest;
import com.example.VO.CategoryVO;

import java.util.List;

public interface CategoryService {
    
    /**
     * 获取所有分类
     */
    List<CategoryVO> getAllCategories();
    
    /**
     * 获取所有启用的分类（小程序端）
     */
    List<CategoryVO> getEnabledCategories();
    
    /**
     * 根据ID获取分类
     */
    CategoryVO getCategoryById(Long id);
    
    /**
     * 添加分类
     */
    void addCategory(CategoryRequest request);
    
    /**
     * 更新分类
     */
    void updateCategory(CategoryRequest request);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long id);
    
    /**
     * 更新分类状态
     */
    void updateCategoryStatus(Long id, Integer status);
}
