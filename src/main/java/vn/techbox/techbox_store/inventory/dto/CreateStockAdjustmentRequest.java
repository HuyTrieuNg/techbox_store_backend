package vn.techbox.techbox_store.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
public class CreateStockAdjustmentRequest {

    @NotBlank(message = "Check name is required")
    @Size(max = 255, message = "Check name must not exceed 255 characters")
    private String checkName;

    private LocalDateTime adjustmentDate;

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<StockAdjustmentItemRequest> items;
}