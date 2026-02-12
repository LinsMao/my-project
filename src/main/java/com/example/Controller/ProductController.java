package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Service.ProductService;
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
}
