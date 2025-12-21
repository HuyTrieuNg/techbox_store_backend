package vn.techbox.techbox_store.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.mapper.StockExportMapper;
import vn.techbox.techbox_store.inventory.model.StockExport;
import vn.techbox.techbox_store.inventory.model.StockExportItem;
import vn.techbox.techbox_store.inventory.repository.StockExportRepository;
import vn.techbox.techbox_store.inventory.service.StockExportService;
import vn.techbox.techbox_store.order.repository.OrderRepository;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockExportServiceImpl implements StockExportService {
    
    private final StockExportRepository stockExportRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StockExportMapper stockExportMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<StockExportDTO> getAllStockExports(
            LocalDate fromDate,
            LocalDate toDate,
            Integer userId,
            Integer orderId,
            String documentCode,
            Pageable pageable) {
        
        log.info("Getting all stock exports with filters - fromDate: {}, toDate: {}, userId: {}, orderId: {}, documentCode: {}", 
                fromDate, toDate, userId, orderId, documentCode);
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
        
        Page<StockExport> stockExports = stockExportRepository.findAllWithFilters(
                fromDateTime, toDateTime, userId, orderId, documentCode, pageable);
        
        Page<StockExportDTO> result = stockExports.map(stockExportMapper::toDTO);
        
        // Populate user and order names/codes for each item
        result.forEach(dto -> {
            if (dto.getUserId() != null) {
                userRepository.findById(dto.getUserId())
                        .ifPresent(user -> dto.setUserName(user.getFirstName() + " " + user.getLastName()));
            }
            if (dto.getOrderId() != null) {
                orderRepository.findById(dto.getOrderId().longValue())
                        .ifPresentOrElse(order -> dto.setOrderCode(order.getOrderCode()),
                                () -> dto.setOrderCode("Đơn hàng #" + dto.getOrderId()));
            }
        });
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public StockExportDetailDTO getStockExportById(Integer id) {
        log.info("Getting stock export by ID: {}", id);
        
        StockExport stockExport = stockExportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock export not found with ID: " + id));
        
        StockExportDetailDTO dto = stockExportMapper.toDetailDTO(stockExport);
        
        // Populate user name
        if (stockExport.getUserId() != null) {
            userRepository.findById(stockExport.getUserId())
                    .ifPresent(user -> dto.setUserName(user.getFirstName() + " " + user.getLastName()));
        }
        
        // Populate order code
        if (stockExport.getOrderId() != null) {
            orderRepository.findById(stockExport.getOrderId().longValue())
                    .ifPresentOrElse(order -> dto.setOrderCode(order.getOrderCode()),
                            () -> dto.setOrderCode("Đơn hàng #" + stockExport.getOrderId()));
        }
        
        return dto;
    }
    
    @Override
    public StockExportDetailDTO createStockExport(CreateStockExportRequest request, Integer currentUserId) {
        log.info("Creating new stock export for user: {}", currentUserId);
        
        // Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Stock export must have at least one item");
        }
        
        // Create stock export entity
        StockExport stockExport = stockExportMapper.toEntity(request, currentUserId);
        
        // Process each item
        for (StockExportItemRequest itemRequest : request.getItems()) {
            // Find product variation
            ProductVariation variation = productVariationRepository.findById(itemRequest.getProductVariationId())
                    .orElseThrow(() -> new RuntimeException("Product variation not found with ID: " 
                            + itemRequest.getProductVariationId()));
            
            // Validate sufficient stock
            if (variation.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product variation ID: " 
                        + itemRequest.getProductVariationId() 
                        + ". Available: " + variation.getStockQuantity() 
                        + ", Required: " + itemRequest.getQuantity());
            }
            
            // Create stock export item (cost price from avg_cost_price)
            StockExportItem item = stockExportMapper.toItemEntity(itemRequest, variation, stockExport);
            stockExport.addItem(item);
            
            // Decrease stock quantity
            decreaseProductVariationStock(variation, itemRequest.getQuantity());
        }
        
        // Calculate total COGS value
        stockExport.calculateTotalCogsValue();
        
        // Save stock export
        StockExport savedStockExport = stockExportRepository.save(stockExport);
        
        log.info("Created stock export with ID: {} and document code: {}", 
                savedStockExport.getId(), savedStockExport.getDocumentCode());
        
        return stockExportMapper.toDetailDTO(savedStockExport);
    }
    
    @Override
    @Transactional
    public StockExportDetailDTO createStockExportFromOrder(
            Integer orderId, 
            CreateStockExportFromOrderRequest request, 
            Integer currentUserId) {
        
        log.info("Creating stock export from order ID: {}", orderId);
        
        // Check if stock export already exists for this order
        Optional<StockExport> existingExport = stockExportRepository.findByOrderId(orderId);
        if (existingExport.isPresent()) {
            throw new RuntimeException("Stock export already exists for order ID: " + orderId);
        }
        
        // Get order details
        vn.techbox.techbox_store.order.model.Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Create stock export request from order items
        CreateStockExportRequest exportRequest = new CreateStockExportRequest();
        exportRequest.setOrderId(orderId);
        exportRequest.setNote(request.getNote() != null ? request.getNote() : "Stock export for order " + order.getOrderCode());
        
        List<StockExportItemRequest> items = order.getOrderItems().stream()
                .map(orderItem -> StockExportItemRequest.builder()
                        .productVariationId(orderItem.getProductVariation().getId())
                        .quantity(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toList());
        exportRequest.setItems(items);
        
        return createStockExport(exportRequest, currentUserId);
    }
    
   
    
    /**
     * Decrease product variation stock when exporting
     */
    private void decreaseProductVariationStock(ProductVariation variation, Integer quantity) {
        Integer currentStock = variation.getStockQuantity();
        Integer newStock = currentStock - quantity;
        
        variation.setStockQuantity(newStock);
        productVariationRepository.save(variation);
        
        log.info("Updated product variation {} - New stock: {}", variation.getId(), newStock);
    }
 
    
}
