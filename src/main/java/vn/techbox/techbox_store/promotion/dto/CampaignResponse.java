package vn.techbox.techbox_store.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
    
    private Integer id;
    
    private String name;
    
    private String description;
    
    private String image;
    
    private String imageID;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Computed fields
    private String status;
    
    private boolean isActive;
    
    private boolean isScheduled;
    
    private boolean isExpired;
    
    // Related data
    private List<PromotionResponse> promotions;
    
    private Integer promotionCount;
}