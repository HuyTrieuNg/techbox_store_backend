package vn.techbox.techbox_store.inventory.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.model.StockExport;
import vn.techbox.techbox_store.inventory.model.StockExportItem;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockExportMapper {
    
    /**
     * Convert StockExport entity to StockExportDTO
     */
    public StockExportDTO toDTO(StockExport stockExport) {
        if (stockExport == null) {
            return null;
        }
        
        return StockExportDTO.builder()
                .id(stockExport.getId())
                .documentCode(stockExport.getDocumentCode())
                .userId(stockExport.getUserId())
                .userName(null) // Will be populated by service if needed
                .orderId(stockExport.getOrderId())
                .orderCode(null) // Will be populated by service if needed
                .exportDate(stockExport.getExportDate())
                .totalCogsValue(stockExport.getTotalCogsValue())
                .note(stockExport.getNote())
                .createdAt(stockExport.getCreatedAt())
                .totalItems(stockExport.getItems() != null ? stockExport.getItems().size() : 0)
                .build();
    }
    
    /**
     * Convert StockExport entity to StockExportDetailDTO
     */
    public StockExportDetailDTO toDetailDTO(StockExport stockExport) {
        if (stockExport == null) {
            return null;
        }
        
        return StockExportDetailDTO.builder()
                .id(stockExport.getId())
                .documentCode(stockExport.getDocumentCode())
                .userId(stockExport.getUserId())
                .userName(null) // Will be populated by service if needed
                .orderId(stockExport.getOrderId())
                .orderCode(null) // Will be populated by service if needed
                .exportDate(stockExport.getExportDate())
                .totalCogsValue(stockExport.getTotalCogsValue())
                .note(stockExport.getNote())
                .createdAt(stockExport.getCreatedAt())
                .items(stockExport.getItems() != null ? 
                       stockExport.getItems().stream()
                           .map(this::toItemDTO)
                           .collect(Collectors.toList()) : null)
                .build();
    }
    
    /**
     * Convert StockExportItem entity to StockExportItemDTO
     */
    public StockExportItemDTO toItemDTO(StockExportItem item) {
        if (item == null) {
            return null;
        }
        
        ProductVariation variation = item.getProductVariation();
        
        return StockExportItemDTO.builder()
                .id(item.getId())
                .productVariationId(variation.getId())
                .productName(variation.getProduct() != null ? 
                            variation.getProduct().getName() : null)
                .variationName(variation.getVariationName())
                .sku(variation.getSku())
                .quantity(item.getQuantity())
                .costPrice(item.getCostPrice())
                .totalValue(item.getTotalValue())
                .build();
    }
    
    /**
     * Convert CreateStockExportRequest to StockExport entity
     */
    public StockExport toEntity(CreateStockExportRequest request, Integer userId) {
        if (request == null) {
            return null;
        }
        
        return StockExport.builder()
                .userId(userId)
                .orderId(request.getOrderId())
                .exportDate(request.getExportDate())
                .note(request.getNote())
                .build();
    }
    
    /**
     * Create StockExportItem from request
     */
    public StockExportItem toItemEntity(StockExportItemRequest request, 
                                         ProductVariation variation,
                                         StockExport stockExport) {
        if (request == null) {
            return null;
        }
        
        // Cost price is taken from product variation's average cost price
        return StockExportItem.builder()
                .stockExport(stockExport)
                .productVariation(variation)
                .quantity(request.getQuantity())
                .costPrice(variation.getAvgCostPrice())
                .build();
    }
}
