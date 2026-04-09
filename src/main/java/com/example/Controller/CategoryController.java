package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.category.CategoryRequest;
import com.example.Service.CategoryService;
import com.example.VO.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有启用的分类（小程序端）
     */
    @GetMapping("/public/list")
    public ApiResponse<List<CategoryVO>> getEnabledCategories() {
        try {
            List<CategoryVO> categories = categoryService.getEnabledCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有分类
     */
    @GetMapping
    public ApiResponse<List<CategoryVO>> getAllCategories() {
        try {
            List<CategoryVO> categories = categoryService.getAllCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryVO> getCategoryById(@PathVariable Long id) {
        try {
            CategoryVO category = categoryService.getCategoryById(id);
            return ApiResponse.success(category);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取分类失败：" + e.getMessage());
        }
    }

    /**
     * 添加分类
     */
    @PostMapping
    public ApiResponse<String> addCategory(@RequestBody CategoryRequest request) {
        try {
            categoryService.addCategory(request);
            return ApiResponse.success("添加分类成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("添加分类失败：" + e.getMessage());
        }
    }

    /**
     * 更新分类
     */
    @PutMapping
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequest request) {
        try {
            categoryService.updateCategory(request);
            return ApiResponse.success("更新分类成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新分类失败：" + e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success("删除分类成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除分类失败：" + e.getMessage());
        }
    }

    /**
     * 更新分类状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateCategoryStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            categoryService.updateCategoryStatus(id, status);
            return ApiResponse.success("更新状态成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新状态失败：" + e.getMessage());
        }
    }
}
