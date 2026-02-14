package com.example.Service.impl;

import com.example.Entity.Product;
import com.example.Mapper.ProductMapper;
import com.example.Service.ProductService;
import com.example.VO.ProductDetailVO;
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

    @Override
    public ProductDetailVO getProductDetail(Long id) {

        if(id==null||id==0){
            throw new IllegalArgumentException("商品ID不合法");
        }

        Product product = productMapper.selectById(id);
        if (product == null){
            throw new RuntimeException("商品不存在或已下架");
        }

        // 组装 VO
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setImage(product.getMainImage());
        vo.setSoldCount(product.getSoldCount());
        vo.setStock(product.getStock());
        vo.setUnit(product.getUnit());
        // 生成标签
        List<String> tags = new ArrayList<>();
        if (product.getIsRecommended() != null && product.getIsRecommended() == 1) tags.add("推荐");
        if (product.getIsHot() != null && product.getIsHot() == 1) tags.add("热销");
        if (product.getIsNew() != null && product.getIsNew() == 1) tags.add("新品");
        vo.setTagList(tags);

        vo.setOriginPlace(product.getOriginPlace());
        vo.setBrand(product.getBrand());
        vo.setShelfLife(product.getShelfLife());
        vo.setDeliveryType(product.getDeliveryType());
        vo.setMinPurchase(product.getMinPurchase());
        vo.setMaxPurchase(product.getMaxPurchase());

        return vo;
    }
}
