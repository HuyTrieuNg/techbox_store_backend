package vn.techbox.techbox_store.promotion.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUpdateRequest {
    
    @Size(min = 3, max = 255, message = "Campaign name must be between 3 and 255 characters")
    private String name;
    
    private String description;
    
    private String image;
    
    private String imageID;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
}