package vn.techbox.techbox_store.inventory.service;

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
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockExportServiceImpl implements StockExportService {
    
    private final StockExportRepository stockExportRepository;
    private final ProductVariationRepository productVariationRepository;
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
        
        return stockExports.map(stockExportMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public StockExportDetailDTO getStockExportById(Integer id) {
        log.info("Getting stock export by ID: {}", id);
        
        StockExport stockExport = stockExportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock export not found with ID: " + id));
        
        return stockExportMapper.toDetailDTO(stockExport);
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
        
        // TODO: Get order items from Order service
        // For now, throw an exception as this requires Order entity integration
        throw new RuntimeException("Order integration not yet implemented. Please use manual stock export creation.");
        
        /* Placeholder for future implementation:
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Create stock export request from order items
        CreateStockExportRequest exportRequest = new CreateStockExportRequest();
        exportRequest.setOrderId(orderId);
        exportRequest.setNote(request.getNote());
        
        List<StockExportItemRequest> items = order.getOrderItems().stream()
                .map(orderItem -> StockExportItemRequest.builder()
                        .productVariationId(orderItem.getProductVariationId())
                        .quantity(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toList());
        exportRequest.setItems(items);
        
        return createStockExport(exportRequest, currentUserId);
        */
    }
    
    @Override
    @Transactional(readOnly = true)
    public StockExportReportDTO generateReport(
            LocalDate fromDate,
            LocalDate toDate,
            String groupBy) {
        
        log.info("Generating stock export report - fromDate: {}, toDate: {}, groupBy: {}", 
                fromDate, toDate, groupBy);
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
        
        List<StockExport> stockExports = stockExportRepository.findForReport(fromDateTime, toDateTime);
        
        // Calculate totals
        int totalDocuments = stockExports.size();
        int totalQuantity = stockExports.stream()
                .flatMap(se -> se.getItems().stream())
                .mapToInt(StockExportItem::getQuantity)
                .sum();
        BigDecimal totalCogsValue = stockExports.stream()
                .map(StockExport::getTotalCogsValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Group data based on groupBy parameter
        List<ReportItemDTO> details = groupReportData(stockExports, groupBy);
        
        return StockExportReportDTO.builder()
                .totalDocuments(totalDocuments)
                .totalQuantity(totalQuantity)
                .totalCogsValue(totalCogsValue)
                .details(details)
                .build();
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
    
    /**
     * Group report data based on groupBy parameter
     */
    private List<ReportItemDTO> groupReportData(List<StockExport> stockExports, String groupBy) {
        if (stockExports.isEmpty()) {
            return new ArrayList<>();
        }
        
        if ("day".equalsIgnoreCase(groupBy)) {
            return groupByDay(stockExports);
        } else if ("month".equalsIgnoreCase(groupBy)) {
            return groupByMonth(stockExports);
        } else if ("product".equalsIgnoreCase(groupBy)) {
            return groupByProduct(stockExports);
        }
        
        // Default: return overall summary
        return Collections.singletonList(createOverallSummary(stockExports));
    }
    
    private List<ReportItemDTO> groupByDay(List<StockExport> stockExports) {
        Map<String, List<StockExport>> grouped = stockExports.stream()
                .collect(Collectors.groupingBy(se -> 
                        se.getExportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> createReportItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    private List<ReportItemDTO> groupByMonth(List<StockExport> stockExports) {
        Map<String, List<StockExport>> grouped = stockExports.stream()
                .collect(Collectors.groupingBy(se -> 
                        se.getExportDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))));
        
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> createReportItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    private List<ReportItemDTO> groupByProduct(List<StockExport> stockExports) {
        // Group by product variation ID
        Map<Integer, List<StockExportItem>> itemsByProduct = stockExports.stream()
                .flatMap(se -> se.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getProductVariation().getId()));
        
        return itemsByProduct.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> createReportItemForProduct(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    private ReportItemDTO createReportItem(String groupKey, List<StockExport> exports) {
        int documentCount = exports.size();
        int totalQuantity = exports.stream()
                .flatMap(se -> se.getItems().stream())
                .mapToInt(StockExportItem::getQuantity)
                .sum();
        BigDecimal totalValue = exports.stream()
                .map(StockExport::getTotalCogsValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return ReportItemDTO.builder()
                .groupKey(groupKey)
                .documentCount(documentCount)
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .build();
    }
    
    private ReportItemDTO createReportItemForProduct(Integer productVariationId, List<StockExportItem> items) {
        int totalQuantity = items.stream()
                .mapToInt(StockExportItem::getQuantity)
                .sum();
        BigDecimal totalValue = items.stream()
                .map(StockExportItem::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String productName = items.isEmpty() ? "Unknown" : 
                items.get(0).getProductVariation().getProduct() != null ?
                items.get(0).getProductVariation().getProduct().getName() : "Unknown";
        String variationName = items.isEmpty() ? "" : 
                items.get(0).getProductVariation().getVariationName();
        
        return ReportItemDTO.builder()
                .groupKey(productName + " - " + variationName)
                .documentCount(items.size())
                .totalQuantity(totalQuantity)
                .totalValue(totalValue)
                .build();
    }
    
    private ReportItemDTO createOverallSummary(List<StockExport> exports) {
        return createReportItem("Overall", exports);
    }
}
