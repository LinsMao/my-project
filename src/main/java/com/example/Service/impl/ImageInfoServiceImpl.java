package com.example.Service.impl;

import com.example.Entity.ImageInfo;
import com.example.Mapper.ImageInfoMapper;
import com.example.Service.ImageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageInfoServiceImpl implements ImageInfoService {

    @Autowired
    private  ImageInfoMapper imageInfoMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.image.banner-path}")
    private String bannerImagePath;


    //根据类型获取图片路径
    @Override
    public List<String> getByTypeImagesUrl(String type) {

       List<ImageInfo> imageInfoList=imageInfoMapper.selectByType(type);

        if (imageInfoList == null || imageInfoList.isEmpty()) {
            return List.of();
        }

        //拼接图片访问路径
        List<String> imageUrlList = new ArrayList<>();
        for (ImageInfo imageInfo : imageInfoList) {
            String fullImageUrl = baseUrl + bannerImagePath + imageInfo.getFileName();
            imageUrlList.add(fullImageUrl);
        }

        return imageUrlList;
    }
}
