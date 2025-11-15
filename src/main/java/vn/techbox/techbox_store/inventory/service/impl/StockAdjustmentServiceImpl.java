package vn.techbox.techbox_store.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.mapper.StockAdjustmentMapper;
import vn.techbox.techbox_store.inventory.model.StockAdjustment;
import vn.techbox.techbox_store.inventory.model.StockAdjustmentItem;
import vn.techbox.techbox_store.inventory.repository.StockAdjustmentRepository;
import vn.techbox.techbox_store.inventory.service.StockAdjustmentService;
import vn.techbox.techbox_store.product.model.ProductVariation;
import vn.techbox.techbox_store.product.repository.ProductVariationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final ProductVariationRepository productVariationRepository;
    private final StockAdjustmentMapper stockAdjustmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StockAdjustmentDTO> getAllStockAdjustments(
            LocalDate fromDate,
            LocalDate toDate,
            Integer userId,
            String checkName,
            Pageable pageable) {

        log.info("Getting all stock adjustments with filters - fromDate: {}, toDate: {}, userId: {}, checkName: {}",
                fromDate, toDate, userId, checkName);

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;

        Page<StockAdjustment> stockAdjustments = stockAdjustmentRepository.findAllWithFilters(
                fromDateTime, toDateTime, userId, checkName, pageable);

        return stockAdjustments.map(stockAdjustmentMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StockAdjustmentDetailDTO getStockAdjustmentDetailById(Integer id) {
        log.info("Getting stock adjustment detail by ID: {}", id);

        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock adjustment not found with ID: " + id));

        return stockAdjustmentMapper.toDetailDTO(stockAdjustment);
    }

    @Override
    public StockAdjustmentDetailDTO createStockAdjustment(CreateStockAdjustmentRequest request, Integer currentUserId) {
        log.info("Creating new stock adjustment for user: {}", currentUserId);

        // Validate items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Stock adjustment must have at least one item");
        }

        // Create stock adjustment entity
        StockAdjustment stockAdjustment = stockAdjustmentMapper.toEntity(request, currentUserId);

        // Process each item
        for (StockAdjustmentItemRequest itemRequest : request.getItems()) {
            // Find product variation
            ProductVariation variation = productVariationRepository.findById(itemRequest.getProductVariationId())
                    .orElseThrow(() -> new RuntimeException("Product variation not found with ID: "
                            + itemRequest.getProductVariationId()));

            // Create stock adjustment item
            StockAdjustmentItem item = stockAdjustmentMapper.toItemEntity(itemRequest, variation, stockAdjustment);
            stockAdjustment.addItem(item);

            // Update product variation inventory
            updateProductVariationInventory(variation, item.getDiffQty());
        }

        // Save stock adjustment
        StockAdjustment savedStockAdjustment = stockAdjustmentRepository.save(stockAdjustment);

        log.info("Created stock adjustment with ID: {}", savedStockAdjustment.getId());
        return stockAdjustmentMapper.toDetailDTO(savedStockAdjustment);
    }

    @Override
    public StockAdjustmentDTO updateStockAdjustment(Integer id, UpdateStockAdjustmentRequest request) {
        log.info("Updating stock adjustment with ID: {}", id);

        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock adjustment not found with ID: " + id));

        // Update basic fields
        if (request.getCheckName() != null) {
            stockAdjustment.setCheckName(request.getCheckName());
        }
        if (request.getAdjustmentDate() != null) {
            stockAdjustment.setAdjustmentDate(request.getAdjustmentDate());
        }
        if (request.getNote() != null) {
            stockAdjustment.setNote(request.getNote());
        }

        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Remove existing items and revert their inventory changes
            for (StockAdjustmentItem existingItem : stockAdjustment.getItems()) {
                updateProductVariationInventory(existingItem.getProductVariation(), -existingItem.getDiffQty());
            }
            stockAdjustment.getItems().clear();

            // Add new items
            for (StockAdjustmentItemRequest itemRequest : request.getItems()) {
                ProductVariation variation = productVariationRepository.findById(itemRequest.getProductVariationId())
                        .orElseThrow(() -> new RuntimeException("Product variation not found with ID: "
                                + itemRequest.getProductVariationId()));

                StockAdjustmentItem item = stockAdjustmentMapper.toItemEntity(itemRequest, variation, stockAdjustment);
                stockAdjustment.addItem(item);

                updateProductVariationInventory(variation, item.getDiffQty());
            }
        }

        StockAdjustment savedStockAdjustment = stockAdjustmentRepository.save(stockAdjustment);
        log.info("Updated stock adjustment with ID: {}", savedStockAdjustment.getId());
        return stockAdjustmentMapper.toDTO(savedStockAdjustment);
    }

    @Override
    public void deleteStockAdjustment(Integer id) {
        log.info("Deleting stock adjustment with ID: {}", id);

        StockAdjustment stockAdjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock adjustment not found with ID: " + id));

        // Revert inventory changes
        for (StockAdjustmentItem item : stockAdjustment.getItems()) {
            updateProductVariationInventory(item.getProductVariation(), -item.getDiffQty());
        }

        stockAdjustmentRepository.delete(stockAdjustment);
        log.info("Deleted stock adjustment with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentDTO> getStockAdjustmentsForReport(LocalDate fromDate, LocalDate toDate) {
        log.info("Getting stock adjustments for report - fromDate: {}, toDate: {}", fromDate, toDate);

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;

        List<StockAdjustment> stockAdjustments = stockAdjustmentRepository.findForReport(fromDateTime, toDateTime);

        return stockAdjustments.stream()
                .map(stockAdjustmentMapper::toDTO)
                .collect(Collectors.toList());
    }



    /**
     * Update product variation inventory based on adjustment
     */
    private void updateProductVariationInventory(ProductVariation variation, Integer diffQty) {
        Integer currentStock = variation.getStockQuantity();
        Integer newStock = currentStock + diffQty;

        if (newStock < 0) {
            throw new RuntimeException("Stock adjustment would result in negative inventory for product: " + variation.getSku());
        }

        variation.setStockQuantity(newStock);
        productVariationRepository.save(variation);

        log.info("Updated inventory for product {}: {} -> {}", variation.getSku(), currentStock, newStock);
    }
}