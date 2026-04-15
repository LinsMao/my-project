package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.category.CategoryRequest;
import com.example.Service.CategoryService;
import com.example.VO.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Value("D:/my-images/banner/")
    private String categoryImageLocalPath;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.image.banner-path}")
    private String categoryImageWebPath;

    /**
     * 上传分类图标
     */
    @PostMapping("/upload-icon")
    public ApiResponse<String> uploadCategoryIcon(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }

            // 1. 确保目录存在
            File dir = new File(categoryImageLocalPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return ApiResponse.error("创建目录失败：" + categoryImageLocalPath);
                }
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponse.error("文件名不能为空");
            }
            
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "category_" + UUID.randomUUID().toString() + ext;
            String filePath = categoryImageLocalPath + fileName;

            // 3. 保存文件
            file.transferTo(new File(filePath));

            // 4. 返回完整URL
            String url = baseUrl + categoryImageWebPath + fileName;
            return ApiResponse.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("图片上传失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("上传异常：" + e.getMessage());
        }
    }

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
     * 获取首页显示的分类（小程序端）
     */
    @GetMapping("/public/home")
    public ApiResponse<List<com.example.VO.HomeCategoryVO>> getHomeCategories() {
        try {
            List<com.example.VO.HomeCategoryVO> categories = categoryService.getHomeCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取首页分类失败：" + e.getMessage());
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
