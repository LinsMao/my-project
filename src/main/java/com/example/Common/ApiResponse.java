package com.example.Common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一API响应格式
 */
@Data
public class ApiResponse<T> implements Serializable {
    private Integer code;    // 状态码
    private String message;  // 提示信息
    private T data;          // 返回数据
    private Long timestamp;  // 时间戳

    // 私有构造方法
    private ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ============ 成功响应 ============

    /**
     * 成功（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 成功（有数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 成功（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    // ============ 失败响应 ============

    /**
     * 失败（默认）
     */
    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(500, "系统异常，请稍后重试", null);
    }

    /**
     * 失败（自定义消息）
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 失败（自定义状态码和消息）
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 失败（自定义状态码、消息和数据）
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    // ============ 常用状态码方法 ============

    /**
     * 参数错误（400）
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null);
    }

    /**
     * 未授权（401）
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }

    /**
     * 禁止访问（403）
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null);
    }

    /**
     * 资源不存在（404）
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }
}
