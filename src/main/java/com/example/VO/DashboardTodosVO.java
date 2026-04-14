package com.example.VO;

import lombok.Data;

@Data
public class DashboardTodosVO {
    private Integer pendingShipOrders;
    private Integer lowStockProducts;
    private Integer pendingAuditProducts;
}
