package vn.techbox.techbox_store.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCalculationResponse {
    
    private Integer productVariationId;
    
    private BigDecimal salePrice;
                
    private String discountType;            // PERCENTAGE hoặc FIXED

    private BigDecimal discountValue;       // Giá trị giảm (% hoặc số tiền)

    private Integer promotionId;            // ID của promotion 

    private Integer campaignId;             // ID của campaign 

}