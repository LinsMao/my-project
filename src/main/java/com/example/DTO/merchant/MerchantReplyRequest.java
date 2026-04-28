package com.example.DTO.merchant;

import lombok.Data;

@Data
public class MerchantReplyRequest {
    
    private Long reviewId;
    
    private String replyContent;
}
