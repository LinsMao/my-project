package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.Service.ImageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/images")
public class ImageInfoController {

    @Autowired
    private ImageInfoService imageInfoService;

    /**
     * 根据类型获取图片URL列表
     * @param type 图片类型（如：BANNER、GOODS、AVATAR）
     */
    @GetMapping("/bannerImagesUrl")
    public ApiResponse<List<String>>  getPathByTypeImageUrl(@RequestParam(value = "type") String type) {

        if (type == null || type.trim().isEmpty()) {
            return ApiResponse.badRequest("类型参数不能为空");
        }

        try {
            List<String> imagesUrlList = imageInfoService.getByTypeImagesUrl(type);
            return ApiResponse.success(imagesUrlList);
        } catch (Exception e) {
            return ApiResponse.error(500, "查询图片地址失败: " + e.getMessage());
        }

    }

}
