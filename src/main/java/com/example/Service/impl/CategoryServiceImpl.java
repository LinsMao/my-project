package com.example.Service.impl;

import com.example.DTO.category.CategoryRequest;
import com.example.Entity.Category;
import com.example.Mapper.CategoryMapper;
import com.example.Service.CategoryService;
import com.example.VO.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVO> voList = new ArrayList<>();
        
        for (Category category : categories) {
            // 更新商品数量
            int productCount = categoryMapper.countProductsByCategoryId(category.getId());
            if (productCount != category.getProductCount()) {
                categoryMapper.updateProductCount(category.getId(), productCount);
                category.setProductCount(productCount);
            }
            
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            voList.add(vo);
        }
        
        return voList;
    }

    @Override
    public List<CategoryVO> getEnabledCategories() {
        List<Category> categories = categoryMapper.selectEnabled();
        List<CategoryVO> voList = new ArrayList<>();
        
        for (Category category : categories) {
            // 更新商品数量
            int productCount = categoryMapper.countProductsByCategoryId(category.getId());
            if (productCount != category.getProductCount()) {
                categoryMapper.updateProductCount(category.getId(), productCount);
                category.setProductCount(productCount);
            }
            
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            voList.add(vo);
        }
        
        return voList;
    }
    
    @Override
    public List<com.example.VO.HomeCategoryVO> getHomeCategories() {
        List<Category> categories = categoryMapper.selectHomeCategories();
        List<com.example.VO.HomeCategoryVO> voList = new ArrayList<>();
        
        for (Category category : categories) {
            // 更新商品数量
            int productCount = categoryMapper.countProductsByCategoryId(category.getId());
            if (productCount != category.getProductCount()) {
                categoryMapper.updateProductCount(category.getId(), productCount);
                category.setProductCount(productCount);
            }
            
            com.example.VO.HomeCategoryVO vo = new com.example.VO.HomeCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setIcon(category.getIcon());
            vo.setDescription(category.getDescription());
            vo.setIsHot(category.getIsHot());
            vo.setProductCount(productCount);
            voList.add(vo);
        }
        
        return voList;
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    @Override
    public void addCategory(CategoryRequest request) {
        // 检查分类名称是否已存在
        Category existing = categoryMapper.selectByName(request.getName());
        if (existing != null) {
            throw new RuntimeException("分类名称已存在");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setIsHot(request.getIsHot() != null ? request.getIsHot() : 0);
        category.setIsShowHome(request.getIsShowHome() != null ? request.getIsShowHome() : 1);
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(CategoryRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        Category category = categoryMapper.selectById(request.getId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 如果修改了名称，检查新名称是否已被其他分类使用
        if (!category.getName().equals(request.getName())) {
            Category existing = categoryMapper.selectByName(request.getName());
            if (existing != null && !existing.getId().equals(request.getId())) {
                throw new RuntimeException("分类名称已存在");
            }
        }
        
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setIsHot(request.getIsHot());
        category.setIsShowHome(request.getIsShowHome());
        category.setSortOrder(request.getSortOrder());
        category.setStatus(request.getStatus());
        
        categoryMapper.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查是否有商品使用该分类
        int productCount = categoryMapper.countProductsByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException("该分类下还有 " + productCount + " 个商品，无法删除");
        }
        
        categoryMapper.deleteById(id);
    }

    @Override
    public void updateCategoryStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        categoryMapper.updateStatus(id, status);
    }
}
