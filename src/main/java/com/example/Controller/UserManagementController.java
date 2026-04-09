package com.example.Controller;

import com.example.Common.ApiResponse;
import com.example.DTO.user.UserListRequest;
import com.example.Entity.User;
import com.example.Service.UserService;
import com.example.VO.UserListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<UserListVO> getUserList(@Valid UserListRequest request) {
        try {
            UserListVO vo = userService.getUserList(request);
            return ApiResponse.success(vo);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ApiResponse.success(user);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<String> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        try {
            userService.toggleUserStatus(id, status);
            return ApiResponse.success("操作成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("操作失败：" + e.getMessage());
        }
    }
}
