package com.guang.resms.entity.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class TeamPerformanceQueryDTO {
    private String teamName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}