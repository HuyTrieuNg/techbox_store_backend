package vn.techbox.techbox_store.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.mapper.StockImportMapper;
import vn.techbox.techbox_store.inventory.model.StockImport;
import vn.techbox.techbox_store.inventory.model.StockImportItem;
import vn.techbox.techbox_store.inventory.repository.StockImportRepository;
import vn.techbox.techbox_store.inventory.repository.SupplierRepository;
import vn.techbox.techbox_store.inventory.service.StockImportService;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockImportServiceImpl implements StockImportService {
    
    private final StockImportRepository stockImportRepository;
    private final ProductVariationRepository productVariationRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final StockImportMapper stockImportMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<StockImportDTO> getAllStockImports(
            LocalDate fromDate,
            LocalDate toDate,
            Integer supplierId,
            Integer userId,
            String documentCode,
            Pageable pageable) {
        
        log.info("Getting all stock imports with filters - fromDate: {}, toDate: {}, supplierId: {}, userId: {}, documentCode: {}", 
                fromDate, toDate, supplierId, userId, documentCode);
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
        
        Page<StockImport> stockImports = stockImportRepository.findAllWithFilters(
                fromDateTime, toDateTime, supplierId, userId, documentCode, pageable);
        
        Page<StockImportDTO> result = stockImports.map(stockImportMapper::toDTO);
        
        // Populate supplier and user names for each item
        result.forEach(dto -> {
            if (dto.getSupplierId() != null) {
                supplierRepository.findById(dto.getSupplierId())
                        .ifPresent(supplier -> dto.setSupplierName(supplier.getName()));
            }
            if (dto.getUserId() != null) {
                userRepository.findById(dto.getUserId())
                        .ifPresent(user -> dto.setUserName(user.getFirstName() + " " + user.getLastName()));
            }
        });
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public StockImportDetailDTO getStockImportById(Integer id) {
        log.info("Getting stock import by ID: {}", id);
        
        StockImport stockImport = stockImportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock import not found with ID: " + id));
        
        StockImportDetailDTO dto = stockImportMapper.toDetailDTO(stockImport);
        
        // Populate supplier name
        if (stockImport.getSupplierId() != null) {
            supplierRepository.findById(stockImport.getSupplierId())
                    .ifPresent(supplier -> dto.setSupplierName(supplier.getName()));
        }
        
        // Populate user name
        if (stockImport.getUserId() != null) {
            userRepository.findById(stockImport.getUserId())
                    .ifPresent(user -> dto.setUserName(user.getFirstName() + " " + user.getLastName()));
        }
        
        return dto;
    }
    
    @Override
    public StockImportDetailDTO createStockImport(CreateStockImportRequest request, Integer currentUserId) {
        log.info("Creating new stock import for user: {}", currentUserId);
        
        // Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Stock import must have at least one item");
        }
        
        // Create stock import entity
        StockImport stockImport = stockImportMapper.toEntity(request, currentUserId);
        
        // Process each item
        for (StockImportItemRequest itemRequest : request.getItems()) {
            // Find product variation
            ProductVariation variation = productVariationRepository.findById(itemRequest.getProductVariationId())
                    .orElseThrow(() -> new RuntimeException("Product variation not found with ID: " 
                            + itemRequest.getProductVariationId()));
            
            // Create stock import item
            StockImportItem item = stockImportMapper.toItemEntity(itemRequest, variation, stockImport);
            stockImport.addItem(item);
            
            // Update product variation inventory
            updateProductVariationInventory(variation, itemRequest.getQuantity(), itemRequest.getCostPrice());
        }
        
        // Calculate total cost value
        stockImport.calculateTotalCostValue();
        
        // Save stock import
        StockImport savedStockImport = stockImportRepository.save(stockImport);
        
        log.info("Created stock import with ID: {} and document code: {}", 
                savedStockImport.getId(), savedStockImport.getDocumentCode());
        
        return stockImportMapper.toDetailDTO(savedStockImport);
    }
    
    @Override
    @Transactional(readOnly = true)
    public StockImportDetailDTO getStockImportByDocumentCode(String documentCode) {
        log.info("Getting stock import by document code: {}", documentCode);
        
        StockImport stockImport = stockImportRepository.findByDocumentCode(documentCode)
                .orElseThrow(() -> new RuntimeException("Stock import not found with document code: " + documentCode));
        
        return stockImportMapper.toDetailDTO(stockImport);
    }
    
    
    /**
     * Update product variation inventory when importing stock
     */
    private void updateProductVariationInventory(ProductVariation variation, Integer quantity, BigDecimal costPrice) {
        // Calculate new average cost price using weighted average
        Integer currentStock = variation.getStockQuantity();
        BigDecimal currentAvgCost = variation.getAvgCostPrice() != null ? 
                variation.getAvgCostPrice() : BigDecimal.ZERO;
        
        BigDecimal currentTotalValue = currentAvgCost.multiply(new BigDecimal(currentStock));
        BigDecimal newTotalValue = costPrice.multiply(new BigDecimal(quantity));
        BigDecimal combinedTotalValue = currentTotalValue.add(newTotalValue);
        
        Integer newStock = currentStock + quantity;
        BigDecimal newAvgCostPrice = newStock > 0 ? 
                combinedTotalValue.divide(new BigDecimal(newStock), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        // Update variation
        variation.setStockQuantity(newStock);
        variation.setAvgCostPrice(newAvgCostPrice);
        
        productVariationRepository.save(variation);
        
        log.info("Updated product variation {} - New stock: {}, New avg cost: {}", 
                variation.getId(), newStock, newAvgCostPrice);
    }
    
    
}
