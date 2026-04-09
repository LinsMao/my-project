package com.example.Service;

import com.example.Entity.Banner;
import com.example.VO.BannerVO;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService {

    /**
     * 获取所有轮播图列表
     * @return 轮播图列表
     */
    List<Banner> getAllBanners();

    /**
     * 根据ID获取轮播图
     * @param id 轮播图ID
     * @return 轮播图信息
     */
    Banner getBannerById(Long id);

    /**
     * 添加轮播图
     * @param banner 轮播图信息
     */
    void addBanner(Banner banner);

    /**
     * 更新轮播图
     * @param banner 轮播图信息
     */
    void updateBanner(Banner banner);

    /**
     * 删除轮播图
     * @param id 轮播图ID
     */
    void deleteBanner(Long id);

    /**
     * 切换轮播图状态
     * @param id 轮播图ID
     * @param status 状态
     */
    void toggleStatus(Long id, Integer status);

    /**
     * 更新轮播图排序
     * @param id 轮播图ID
     * @param sortOrder 排序值
     */
    void updateSortOrder(Long id, Integer sortOrder);

    /**
     * 获取启用的轮播图列表（小程序端）
     * @return 轮播图列表
     */
    List<Banner> getActiveBanners();

    /**
     * 获取轮播图信息（小程序端VO格式）
     * @return 轮播图VO列表
     */
    List<BannerVO> getBannerInfo();
}
