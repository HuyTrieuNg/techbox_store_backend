package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductDTO {
    private Integer productId;
    private String productName;
    private String spu;
    private String imageUrl;
    private Long totalSold;
    private BigDecimal revenue;
    private Double averageRating;
}
