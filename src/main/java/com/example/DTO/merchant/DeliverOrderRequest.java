package com.example.DTO.merchant;

import lombok.Data;

@Data
public class DeliverOrderRequest {
    private String deliveryCompany;
    private String deliveryNo;
}
