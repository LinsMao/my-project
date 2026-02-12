package com.example.Service;

import com.example.VO.ProductVO;

import java.util.List;

public interface ProductService {

    /**
     * 获取首页商品列表
     */
    List<ProductVO> getHomeProductPage(int page, int size);
}
