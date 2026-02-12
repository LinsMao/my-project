package com.example.Service.impl;

import com.example.Entity.Product;
import com.example.Mapper.ProductMapper;
import com.example.Service.ProductService;
import com.example.VO.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductVO> getHomeProductPage(int page, int size) {

        // 参数兜底（防止前端乱传）
        if (page < 1) page = 1;
        if (size <= 0) size = 10;

        int offset = (page - 1) * size;

        List<Product> products =
                productMapper.selectHomeProductPage(offset, size);

        List<ProductVO> result = new ArrayList<>();

        for (Product p : products) {
            ProductVO vo = new ProductVO();

            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setSubtitle(p.getSubtitle());
            vo.setPrice(p.getPrice());
            vo.setOriginalPrice(p.getOriginalPrice());
            vo.setSoldCount(p.getSoldCount());
            vo.setUnit(p.getUnit());

            // 主图：不拼接 URL
            vo.setImage(p.getMainImage());

            // 首页标签
            List<String> tags = new ArrayList<>();
            if (p.getIsRecommended() != null && p.getIsRecommended() == 1) {
                tags.add("推荐");
            }
            if (p.getIsHot() != null && p.getIsHot() == 1) {
                tags.add("热销");
            }
            if (p.getIsNew() != null && p.getIsNew() == 1) {
                tags.add("新品");
            }
            vo.setTagList(tags);

            result.add(vo);
        }

        return result;
    }
}
