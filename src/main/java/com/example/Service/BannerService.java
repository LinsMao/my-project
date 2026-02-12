package com.example.Service;

import com.example.Entity.Banner;
import com.example.VO.BannerVO;

import java.util.List;

public interface BannerService {
    /**
     * 获取所有启用的轮播图（前端用）
     */
    List<BannerVO> getBannerInfo();
}
