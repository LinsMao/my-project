package com.example.VO;

import lombok.Data;

@Data
public class DashboardStatsVO {
    private Integer todayOrders;
    private Double todayOrdersGrowth;
    private Double todaySales;
    private Double todaySalesGrowth;
    private Integer pendingOrders;
    private Integer totalProducts;
    private Integer onSaleProducts;
}
