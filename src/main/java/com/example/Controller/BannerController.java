package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Service.BannerService;
import com.example.VO.BannerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取所有启用的轮播图（前端用）
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

}
