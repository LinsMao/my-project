package com.example.Mapper;

import com.example.Entity.ImageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImageInfoMapper {
    List<ImageInfo> selectByType(
            @Param("type") String type
    );
}
