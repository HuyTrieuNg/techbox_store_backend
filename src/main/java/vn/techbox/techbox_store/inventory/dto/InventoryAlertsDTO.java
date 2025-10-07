package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertsDTO {
    private List<ProductVariationDTO> outOfStock;
    private List<ProductVariationDTO> lowStock;
    private List<ProductVariationDTO> overstock;
}
