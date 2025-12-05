package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentDetailDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String documentCode;
    private String checkName;
    private LocalDateTime adjustmentDate;
    private String note;
    private LocalDateTime createdAt;
    private List<StockAdjustmentItemDTO> items;
}