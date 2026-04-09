package com.example.Mapper;

import com.example.Entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 轮播图 Mapper 接口
 */
@Mapper
public interface BannerMapper {

    /**
     * 查询所有轮播图列表（按排序）
     * @return 轮播图列表
     */
    List<Banner> selectAll();

    /**
     * 根据ID查询轮播图
     * @param id 轮播图ID
     * @return 轮播图信息
     */
    Banner selectById(@Param("id") Long id);

    /**
     * 添加轮播图
     * @param banner 轮播图信息
     * @return 影响行数
     */
    int insert(Banner banner);

    /**
     * 更新轮播图
     * @param banner 轮播图信息
     * @return 影响行数
     */
    int update(Banner banner);

    /**
     * 删除轮播图
     * @param id 轮播图ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新轮播图状态
     * @param id 轮播图ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新轮播图排序
     * @param id 轮播图ID
     * @param sortOrder 排序值
     * @return 影响行数
     */
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 查询启用的轮播图列表（小程序端使用）
     * @return 轮播图列表
     */
    List<Banner> selectActiveList();
}
