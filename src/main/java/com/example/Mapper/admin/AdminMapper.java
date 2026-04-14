package com.example.Mapper.admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.Entity.Admin;
import com.example.DTO.admin.AdminListRequest;
import java.util.List;

@Mapper
public interface AdminMapper {

    // 根据用户名查询
    Admin findByUsername(String username);

    // 根据邮箱查询
    Admin findByEmail(String email);

    // 根据ID查询
    Admin findById(Long id);

    // 插入
    void insert(Admin admin);

    // 更新最后登录时间
    void updateLastLoginTime(Long id);

    // 根据条件查询商家列表
    List<Admin> findByConditions(AdminListRequest request);

    // 统计符合条件的总数
    Long countByConditions(AdminListRequest request);

    // 更新个人信息
    void updateProfile(Admin admin);

    // 更新密码
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    // 更新状态
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 查询商家的商品数量
    Integer countProductsByMerchantId(@Param("merchantId") Long merchantId);
    
    // Dashboard 统计方法
    Integer countTotalMerchants();
    Integer countTotalUsers();
    List<Admin> selectRecentMerchants(@Param("limit") Integer limit);
}
