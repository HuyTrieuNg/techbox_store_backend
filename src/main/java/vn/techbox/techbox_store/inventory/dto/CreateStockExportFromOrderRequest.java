package vn.techbox.techbox_store.inventory.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStockExportFromOrderRequest {
    
    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;
}
