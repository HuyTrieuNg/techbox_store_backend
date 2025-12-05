package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String documentCode;
    private String checkName;
    private LocalDateTime adjustmentDate;
    private String note;
    private LocalDateTime createdAt;
    private Integer totalItems;
}