package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.RegisterRequest;
import com.example.DTO.WxLoginRequest;
import com.example.Entity.User;
import com.example.Service.UserService;
import com.example.Utils.JwtUtils;
import com.example.VO.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("D:/my-images/users/")
    private String avatarLocalPath;

    @Value("${app.image.users-path}")
    private String avatarWebPath;

    @Value("${app.base-url}")
    private String baseUrl;


    /**
     * 微信登录
     * @param request 操作凭证
     */
    @PostMapping("/wx/login")
    public ApiResponse<LoginVO> wxLogin(@RequestBody WxLoginRequest request) {
        try {
            LoginVO vo = userService.wxLogin(request.getCode());
            return ApiResponse.success(vo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 注册用户信息
     */
    @PostMapping("/register")
    public ApiResponse<?> register(HttpServletRequest request, @RequestBody RegisterRequest registerRequest) {
        // 从 token 中获取用户ID
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.unauthorized("未登录");
        }
        token = token.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return ApiResponse.unauthorized("token无效");
        }
        Long userId = JwtUtils.getUserIdFromToken(token);

        try {
            userService.register(userId,registerRequest);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * 保存头像生成url
     */
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 确保目录存在
            File dir = new File(avatarLocalPath);
            if (!dir.exists()) dir.mkdirs();

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + ext;
            String filePath = avatarLocalPath + fileName;

            // 3. 保存文件
            file.transferTo(new File(filePath));

            // 4. 构建可访问的 URL
            String url = baseUrl + avatarWebPath + fileName;
            return ApiResponse.success(url);
        } catch (IOException e) {
            return ApiResponse.error("头像上传失败：" + e.getMessage());
        }
    }


}
