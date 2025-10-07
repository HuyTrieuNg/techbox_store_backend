package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockExportDTO {
    private Integer id;
    private String documentCode;
    private Integer userId;
    private String userName;
    private Integer orderId;
    private String orderCode;
    private LocalDateTime exportDate;
    private BigDecimal totalCogsValue;
    private String note;
    private LocalDateTime createdAt;
    private Integer totalItems;
}
