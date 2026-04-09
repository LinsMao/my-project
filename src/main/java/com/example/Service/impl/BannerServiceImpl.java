package com.example.Service.impl;

import com.example.Entity.Banner;
import com.example.Mapper.BannerMapper;
import com.example.Service.BannerService;
import com.example.VO.BannerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮播图服务实现类
 */
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    /**
     * 获取所有轮播图列表
     * @return 轮播图列表
     */
    @Override
    public List<Banner> getAllBanners() {
        return bannerMapper.selectAll();
    }

    /**
     * 根据ID获取轮播图
     * @param id 轮播图ID
     * @return 轮播图信息
     */
    @Override
    public Banner getBannerById(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        return banner;
    }

    /**
     * 添加轮播图
     * @param banner 轮播图信息
     */
    @Override
    @Transactional
    public void addBanner(Banner banner) {
        // 设置默认值
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        
        int result = bannerMapper.insert(banner);
        if (result == 0) {
            throw new RuntimeException("添加轮播图失败");
        }
    }

    /**
     * 更新轮播图
     * @param banner 轮播图信息
     */
    @Override
    @Transactional
    public void updateBanner(Banner banner) {
        Banner existBanner = bannerMapper.selectById(banner.getId());
        if (existBanner == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        
        int result = bannerMapper.update(banner);
        if (result == 0) {
            throw new RuntimeException("更新轮播图失败");
        }
    }

    /**
     * 删除轮播图
     * @param id 轮播图ID
     */
    @Override
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        
        int result = bannerMapper.deleteById(id);
        if (result == 0) {
            throw new RuntimeException("删除轮播图失败");
        }
    }

    /**
     * 切换轮播图状态
     * @param id 轮播图ID
     * @param status 状态
     */
    @Override
    @Transactional
    public void toggleStatus(Long id, Integer status) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        
        int result = bannerMapper.updateStatus(id, status);
        if (result == 0) {
            throw new RuntimeException("更新状态失败");
        }
    }

    /**
     * 更新轮播图排序
     * @param id 轮播图ID
     * @param sortOrder 排序值
     */
    @Override
    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new IllegalArgumentException("轮播图不存在");
        }
        
        int result = bannerMapper.updateSortOrder(id, sortOrder);
        if (result == 0) {
            throw new RuntimeException("更新排序失败");
        }
    }

    /**
     * 获取启用的轮播图列表（小程序端）
     * @return 轮播图列表
     */
    @Override
    public List<Banner> getActiveBanners() {
        return bannerMapper.selectActiveList();
    }

    /**
     * 获取轮播图信息（小程序端VO格式）
     * @return 轮播图VO列表
     */
    @Override
    public List<BannerVO> getBannerInfo() {
        List<Banner> banners = bannerMapper.selectActiveList();
        return banners.stream().map(banner -> {
            BannerVO vo = new BannerVO();
            // image_path现在存储的是完整URL，直接使用
            vo.setImage(banner.getImagePath());
            vo.setTitle(banner.getTitle());
            vo.setSubtitle(banner.getSubtitle());
            vo.setPrice(banner.getPrice());
            vo.setLinkUrl(banner.getLinkUrl());
            return vo;
        }).collect(Collectors.toList());
    }
}
