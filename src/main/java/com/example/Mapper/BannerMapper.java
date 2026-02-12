package com.example.Mapper;

import com.example.Entity.Banner;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface BannerMapper {
    List<Banner> selectBannerInfo();
}
