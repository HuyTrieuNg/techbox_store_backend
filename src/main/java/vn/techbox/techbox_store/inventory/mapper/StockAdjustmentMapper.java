package vn.techbox.techbox_store.inventory.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.model.StockAdjustment;
import vn.techbox.techbox_store.inventory.model.StockAdjustmentItem;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockAdjustmentMapper {

    /**
     * Convert StockAdjustment entity to StockAdjustmentDTO
     */
    public StockAdjustmentDTO toDTO(StockAdjustment stockAdjustment) {
        if (stockAdjustment == null) {
            return null;
        }

        return StockAdjustmentDTO.builder()
                .id(stockAdjustment.getId())
                .documentCode(stockAdjustment.getDocumentCode())
                .userId(stockAdjustment.getUserId())
                .userName(null) // Will be populated by service if needed
                .checkName(stockAdjustment.getCheckName())
                .adjustmentDate(stockAdjustment.getAdjustmentDate())
                .note(stockAdjustment.getNote())
                .createdAt(stockAdjustment.getCreatedAt())
                .totalItems(stockAdjustment.getItems() != null ? stockAdjustment.getItems().size() : 0)
                .build();
    }

    /**
     * Convert StockAdjustment entity to StockAdjustmentDetailDTO
     */
    public StockAdjustmentDetailDTO toDetailDTO(StockAdjustment stockAdjustment) {
        if (stockAdjustment == null) {
            return null;
        }

        return StockAdjustmentDetailDTO.builder()
                .id(stockAdjustment.getId())
                .documentCode(stockAdjustment.getDocumentCode())
                .userId(stockAdjustment.getUserId())
                .userName(null) // Will be populated by service if needed
                .checkName(stockAdjustment.getCheckName())
                .adjustmentDate(stockAdjustment.getAdjustmentDate())
                .note(stockAdjustment.getNote())
                .createdAt(stockAdjustment.getCreatedAt())
                .items(stockAdjustment.getItems() != null ?
                       stockAdjustment.getItems().stream()
                           .map(this::toItemDTO)
                           .collect(Collectors.toList()) : null)
                .build();
    }

    /**
     * Convert StockAdjustmentItem entity to StockAdjustmentItemDTO
     */
    public StockAdjustmentItemDTO toItemDTO(StockAdjustmentItem item) {
        if (item == null) {
            return null;
        }

        ProductVariation variation = item.getProductVariation();

        return StockAdjustmentItemDTO.builder()
                .id(item.getId())
                .productVariationId(variation.getId())
                .productName(variation.getProduct() != null ?
                            variation.getProduct().getName() : null)
                .variationName(variation.getVariationName())
                .sku(variation.getSku())
                .systemQty(item.getSystemQty())
                .realQty(item.getRealQty())
                .diffQty(item.getDiffQty())
                .costPrice(item.getCostPrice())
                .diffValue(item.getDiffValue())
                .build();
    }

    /**
     * Convert CreateStockAdjustmentRequest to StockAdjustment entity
     */
    public StockAdjustment toEntity(CreateStockAdjustmentRequest request, Integer userId) {
        if (request == null) {
            return null;
        }

        StockAdjustment stockAdjustment = StockAdjustment.builder()
                .userId(userId)
                .checkName(request.getCheckName())
                .adjustmentDate(request.getAdjustmentDate())
                .note(request.getNote())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return stockAdjustment;
    }

    /**
     * Convert StockAdjustmentItemRequest to StockAdjustmentItem entity
     */
    public StockAdjustmentItem toItemEntity(StockAdjustmentItemRequest request,
                                           ProductVariation variation,
                                           StockAdjustment stockAdjustment) {
        if (request == null || variation == null) {
            return null;
        }

        Integer systemQty = variation.getStockQuantity();
        Integer realQty = request.getRealQty();
        Integer diffQty = realQty - systemQty;

        StockAdjustmentItem item = StockAdjustmentItem.builder()
                .productVariation(variation)
                .systemQty(systemQty)
                .realQty(realQty)
                .diffQty(diffQty)
                .costPrice(request.getCostPrice())
                .build();

        item.setStockAdjustment(stockAdjustment);
        return item;
    }
}