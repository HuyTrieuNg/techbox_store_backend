package vn.techbox.techbox_store.inventory.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.model.StockImport;
import vn.techbox.techbox_store.inventory.model.StockImportItem;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockImportMapper {
    
    /**
     * Convert StockImport entity to StockImportDTO
     */
    public StockImportDTO toDTO(StockImport stockImport) {
        if (stockImport == null) {
            return null;
        }
        
        return StockImportDTO.builder()
                .id(stockImport.getId())
                .documentCode(stockImport.getDocumentCode())
                .userId(stockImport.getUserId())
                .userName(null) // Will be populated by service if needed
                .importDate(stockImport.getImportDate())
                .supplierId(stockImport.getSupplierId())
                .supplierName(null) // Will be populated by service if needed
                .totalCostValue(stockImport.getTotalCostValue())
                .note(stockImport.getNote())
                .createdAt(stockImport.getCreatedAt())
                .totalItems(stockImport.getItems() != null ? stockImport.getItems().size() : 0)
                .build();
    }
    
    /**
     * Convert StockImport entity to StockImportDetailDTO
     */
    public StockImportDetailDTO toDetailDTO(StockImport stockImport) {
        if (stockImport == null) {
            return null;
        }
        
        return StockImportDetailDTO.builder()
                .id(stockImport.getId())
                .documentCode(stockImport.getDocumentCode())
                .userId(stockImport.getUserId())
                .userName(null) // Will be populated by service if needed
                .importDate(stockImport.getImportDate())
                .supplierId(stockImport.getSupplierId())
                .supplierName(null) // Will be populated by service if needed
                .totalCostValue(stockImport.getTotalCostValue())
                .note(stockImport.getNote())
                .createdAt(stockImport.getCreatedAt())
                .items(stockImport.getItems() != null ? 
                       stockImport.getItems().stream()
                           .map(this::toItemDTO)
                           .collect(Collectors.toList()) : null)
                .build();
    }
    
    /**
     * Convert StockImportItem entity to StockImportItemDTO
     */
    public StockImportItemDTO toItemDTO(StockImportItem item) {
        if (item == null) {
            return null;
        }
        
        ProductVariation variation = item.getProductVariation();
        
        return StockImportItemDTO.builder()
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
     * Convert CreateStockImportRequest to StockImport entity
     */
    public StockImport toEntity(CreateStockImportRequest request, Integer userId) {
        if (request == null) {
            return null;
        }
        
        StockImport stockImport = StockImport.builder()
                .userId(userId)
                .supplierId(request.getSupplierId())
                .importDate(request.getImportDate())
                .note(request.getNote())
                .build();
        
        return stockImport;
    }
    
    /**
     * Create StockImportItem from request
     */
    public StockImportItem toItemEntity(StockImportItemRequest request, 
                                         ProductVariation variation,
                                         StockImport stockImport) {
        if (request == null) {
            return null;
        }
        
        return StockImportItem.builder()
                .stockImport(stockImport)
                .productVariation(variation)
                .quantity(request.getQuantity())
                .costPrice(request.getCostPrice())
                .build();
    }
}
