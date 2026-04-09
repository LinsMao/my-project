package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Entity.Banner;
import com.example.Service.BannerService;
import com.example.VO.BannerVO;
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
@RequestMapping("/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Value("D:/my-images/banner/")
    private String bannerImageLocalPath;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.image.banner-path}")
    private String bannerImageWebPath;

    /**
     * 上传轮播图图片
     */
    @PostMapping("/upload-image")
    public ApiResponse<String> uploadBannerImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }

            // 1. 确保目录存在
            File dir = new File(bannerImageLocalPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return ApiResponse.error("创建目录失败：" + bannerImageLocalPath);
                }
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ApiResponse.error("文件名不能为空");
            }
            
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + ext;
            String filePath = bannerImageLocalPath + fileName;

            // 3. 保存文件
            file.transferTo(new File(filePath));

            // 4. 返回完整URL
            String url = baseUrl + bannerImageWebPath + fileName;
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
     * 获取所有启用的轮播图（小程序端）
     */
    @GetMapping("/enabled")
    public ApiResponse<List<BannerVO>> getEnabledBanners() {
        try {
            List<BannerVO> banners = bannerService.getBannerInfo();
            return ApiResponse.success(banners);
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败");
        }
    }

    /**
     * 获取所有轮播图列表（管理端）
     */
    @GetMapping("/admin/list")
    public ApiResponse<List<Banner>> getAllBanners() {
        try {
            List<Banner> banners = bannerService.getAllBanners();
            return ApiResponse.success(banners);
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取轮播图详情（管理端）
     */
    @GetMapping("/admin/{id}")
    public ApiResponse<Banner> getBannerById(@PathVariable Long id) {
        try {
            Banner banner = bannerService.getBannerById(id);
            return ApiResponse.success(banner);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加轮播图（管理端）
     */
    @PostMapping("/admin")
    public ApiResponse<String> addBanner(@RequestBody Banner banner) {
        try {
            bannerService.addBanner(banner);
            return ApiResponse.success("添加成功");
        } catch (Exception e) {
            return ApiResponse.error("添加轮播图失败：" + e.getMessage());
        }
    }

    /**
     * 更新轮播图（管理端）
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<String> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        try {
            banner.setId(id);
            bannerService.updateBanner(banner);
            return ApiResponse.success("更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新轮播图失败：" + e.getMessage());
        }
    }

    /**
     * 删除轮播图（管理端）
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteBanner(@PathVariable Long id) {
        try {
            bannerService.deleteBanner(id);
            return ApiResponse.success("删除成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除轮播图失败：" + e.getMessage());
        }
    }

    /**
     * 切换轮播图状态（管理端）
     */
    @PutMapping("/admin/{id}/status")
    public ApiResponse<String> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            bannerService.toggleStatus(id, status);
            return ApiResponse.success("状态更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新状态失败：" + e.getMessage());
        }
    }

    /**
     * 更新轮播图排序（管理端）
     */
    @PutMapping("/admin/{id}/sort")
    public ApiResponse<String> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        try {
            bannerService.updateSortOrder(id, sortOrder);
            return ApiResponse.success("排序更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新排序失败：" + e.getMessage());
        }
    }

}
