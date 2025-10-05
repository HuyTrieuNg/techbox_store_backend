package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    private LocalDateTime date;
    private String type; // IMPORT|EXPORT|ADJUSTMENT
    private String documentCode;
    private Integer documentId;
    private Integer quantity; // + for import, - for export
    private BigDecimal costPrice;
    private Integer balanceAfter;
    private String note;
}
