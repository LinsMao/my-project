package com.example.DTO.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度在 3 到 20 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在 6 到 20 个字符")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    private String nickname;
}
