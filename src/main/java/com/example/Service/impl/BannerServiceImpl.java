package com.example.Service.impl;

import com.example.Entity.Banner;
import com.example.Entity.ImageInfo;
import com.example.Mapper.BannerMapper;
import com.example.Service.BannerService;
import com.example.VO.BannerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerMapper bannerMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.image.banner-path}")
    private String bannerImagePath;



    @Override
    public List<BannerVO> getBannerInfo() {

        List<Banner> bannerList=bannerMapper.selectBannerInfo();




        // 转换为BannerVO并拼接URL
        List<BannerVO> result = new ArrayList<>();
        for (Banner banner : bannerList) {
            BannerVO bannerVO = new BannerVO();

            // 构建完整图片URL
            String fullImageUrl = baseUrl + bannerImagePath + banner.getImageName();
            bannerVO.setImage(fullImageUrl);

            // 设置其他字段
            bannerVO.setTitle(banner.getTitle());
            bannerVO.setSubtitle(banner.getSubtitle());
            bannerVO.setPrice(banner.getPrice());
            bannerVO.setLinkUrl(banner.getLinkUrl());

            result.add(bannerVO);
        }

        return result;


    }
}
