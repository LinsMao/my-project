package com.example.Entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogisticsTrace {
    private Long id;
    private String orderNo;
    private String traceStatus;
    private String traceDesc;
    private String location;
    private LocalDateTime traceTime;
    private LocalDateTime createTime;
}
