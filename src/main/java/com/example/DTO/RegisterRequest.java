package com.example.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nickname;
    private String avatar;
    private Integer gender;
}