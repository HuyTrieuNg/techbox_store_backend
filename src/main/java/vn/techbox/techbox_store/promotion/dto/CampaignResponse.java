package vn.techbox.techbox_store.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    

    private Integer promotionCount;
}