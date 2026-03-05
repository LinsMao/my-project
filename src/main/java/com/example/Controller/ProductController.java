package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Service.ProductService;
import com.example.VO.ProductDetailVO;
import com.example.VO.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 首页商品展示
     */
    @GetMapping("/list")
    public ApiResponse<List<ProductVO>> getProductList( @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        try {
            List<ProductVO> list = productService.getHomeProductPage(page, size);
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error(e+"获取商品列表失败！！！");
        } 
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情VO
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailVO> getProductDetail(@PathVariable Long id){
        try {
            ProductDetailVO detail = productService.getProductDetail(id);
            return ApiResponse.success(detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    @GetMapping("/search")
    public ApiResponse<List<ProductVO>> searchProducts(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ApiResponse.badRequest("搜索关键词不能为空");
            }
            List<ProductVO> products = productService.searchProducts(keyword);
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error("搜索失败：" + e.getMessage());
        }
    }

}
