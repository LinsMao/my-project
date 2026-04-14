package com.example.VO;

import lombok.Data;

@Data
public class AdminDashboardStatsVO {
    private Integer todayOrders;
    private Double todayOrdersGrowth;
    private Double todaySales;
    private Double todaySalesGrowth;
    private Integer totalMerchants;
    private Integer totalUsers;
    private Integer totalProducts;
    private Integer onSaleProducts;
}
