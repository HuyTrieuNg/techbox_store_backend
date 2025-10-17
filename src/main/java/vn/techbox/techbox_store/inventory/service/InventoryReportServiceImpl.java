package vn.techbox.techbox_store.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.inventory.dto.*;
import vn.techbox.techbox_store.inventory.model.StockExport;
import vn.techbox.techbox_store.inventory.model.StockExportItem;
import vn.techbox.techbox_store.inventory.model.StockImport;
import vn.techbox.techbox_store.inventory.model.StockImportItem;
import vn.techbox.techbox_store.inventory.repository.InventoryReportRepository;
import vn.techbox.techbox_store.inventory.repository.StockExportItemRepository;
import vn.techbox.techbox_store.inventory.repository.StockExportRepository;
import vn.techbox.techbox_store.inventory.repository.StockImportItemRepository;
import vn.techbox.techbox_store.inventory.repository.StockImportRepository;
import vn.techbox.techbox_store.inventory.service.impl.InventoryReportService;
import vn.techbox.techbox_store.product.model.Product;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository;
    private final StockImportRepository stockImportRepository;
    private final StockImportItemRepository stockImportItemRepository;
    private final StockExportRepository stockExportRepository;
    private final StockExportItemRepository stockExportItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockBalanceDTO> getStockBalance(
            Integer categoryId,
            Integer brandId,
            String keyword,
            Boolean lowStock,
            Boolean outOfStock
    ) {
        List<ProductVariation> variations = inventoryReportRepository.findStockBalance(
                categoryId,
                brandId,
                keyword,
                lowStock != null && lowStock,
                outOfStock != null && outOfStock
        );

        return variations.stream()
                .map(this::toStockBalanceDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementDTO> getProductHistory(
            Integer productVariationId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        List<StockMovementDTO> movements = new ArrayList<>();

        LocalDateTime startDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime endDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;

        // Get import movements
        List<StockImport> imports = stockImportRepository.findAll().stream()
                .filter(imp -> (startDateTime == null || !imp.getImportDate().isBefore(startDateTime)) &&
                              (endDateTime == null || !imp.getImportDate().isAfter(endDateTime)))
                .collect(Collectors.toList());

        for (StockImport imp : imports) {
            List<StockImportItem> items = stockImportItemRepository.findByStockImportId(imp.getId());
            for (StockImportItem item : items) {
                if (item.getProductVariation().getId().equals(productVariationId)) {
                    movements.add(StockMovementDTO.builder()
                            .date(imp.getImportDate())
                            .type("IMPORT")
                            .documentCode(imp.getDocumentCode())
                            .documentId(imp.getId())
                            .quantity(item.getQuantity())
                            .costPrice(item.getCostPrice())
                            .balanceAfter(null) // Will be calculated later
                            .note(imp.getNote())
                            .build());
                }
            }
        }

        // Get export movements
        List<StockExport> exports = stockExportRepository.findAll().stream()
                .filter(exp -> (startDateTime == null || !exp.getExportDate().isBefore(startDateTime)) &&
                              (endDateTime == null || !exp.getExportDate().isAfter(endDateTime)))
                .collect(Collectors.toList());

        for (StockExport exp : exports) {
            List<StockExportItem> items = stockExportItemRepository.findByStockExportId(exp.getId());
            for (StockExportItem item : items) {
                if (item.getProductVariation().getId().equals(productVariationId)) {
                    movements.add(StockMovementDTO.builder()
                            .date(exp.getExportDate())
                            .type("EXPORT")
                            .documentCode(exp.getDocumentCode())
                            .documentId(exp.getId())
                            .quantity(-item.getQuantity()) // Negative for export
                            .costPrice(item.getCostPrice())
                            .balanceAfter(null) // Will be calculated later
                            .note(exp.getNote())
                            .build());
                }
            }
        }

        // Sort by date
        movements.sort(Comparator.comparing(StockMovementDTO::getDate));

        // Calculate running balance
        int runningBalance = 0;
        for (StockMovementDTO movement : movements) {
            runningBalance += movement.getQuantity();
            movement.setBalanceAfter(runningBalance);
        }

        return movements;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockValueReportDTO> getStockValueReport(
            LocalDate fromDate,
            LocalDate toDate,
            String groupBy
    ) {
        Map<LocalDate, StockValueReportDTO> reportMap = new TreeMap<>();

        LocalDateTime startDateTime = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        // Get all imports in date range
        List<StockImport> imports = stockImportRepository.findAll().stream()
                .filter(imp -> !imp.getImportDate().isBefore(startDateTime) &&
                              !imp.getImportDate().isAfter(endDateTime))
                .collect(Collectors.toList());

        for (StockImport imp : imports) {
            LocalDate reportDate = getReportDate(imp.getImportDate().toLocalDate(), groupBy);
            StockValueReportDTO report = reportMap.computeIfAbsent(reportDate, d ->
                    StockValueReportDTO.builder()
                            .date(d)
                            .totalImportValue(BigDecimal.ZERO)
                            .totalExportValue(BigDecimal.ZERO)
                            .totalStockValue(BigDecimal.ZERO)
                            .netChange(BigDecimal.ZERO)
                            .build()
            );

            List<StockImportItem> items = stockImportItemRepository.findByStockImportId(imp.getId());
            BigDecimal importValue = items.stream()
                    .map(item -> item.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            report.setTotalImportValue(report.getTotalImportValue().add(importValue));
        }

        // Get all exports in date range
        List<StockExport> exports = stockExportRepository.findAll().stream()
                .filter(exp -> !exp.getExportDate().isBefore(startDateTime) &&
                              !exp.getExportDate().isAfter(endDateTime))
                .collect(Collectors.toList());

        for (StockExport exp : exports) {
            LocalDate reportDate = getReportDate(exp.getExportDate().toLocalDate(), groupBy);
            StockValueReportDTO report = reportMap.computeIfAbsent(reportDate, d ->
                    StockValueReportDTO.builder()
                            .date(d)
                            .totalImportValue(BigDecimal.ZERO)
                            .totalExportValue(BigDecimal.ZERO)
                            .totalStockValue(BigDecimal.ZERO)
                            .netChange(BigDecimal.ZERO)
                            .build()
            );

            List<StockExportItem> items = stockExportItemRepository.findByStockExportId(exp.getId());
            BigDecimal exportValue = items.stream()
                    .map(item -> item.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            report.setTotalExportValue(report.getTotalExportValue().add(exportValue));
        }

        // Calculate net change and total stock value
        List<ProductVariation> allVariations = inventoryReportRepository.findAll().stream()
                .filter(pv -> pv.getDeletedAt() == null)
                .collect(Collectors.toList());

        BigDecimal totalStockValue = allVariations.stream()
                .map(pv -> {
                    BigDecimal avgCost = pv.getAvgCostPrice() != null ? pv.getAvgCostPrice() : BigDecimal.ZERO;
                    return avgCost.multiply(BigDecimal.valueOf(pv.getStockQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Set total stock value for the last date
        if (!reportMap.isEmpty()) {
            LocalDate lastDate = reportMap.keySet().stream().max(LocalDate::compareTo).orElse(null);
            if (lastDate != null) {
                reportMap.get(lastDate).setTotalStockValue(totalStockValue);
            }
        }

        // Calculate net change for each report
        for (StockValueReportDTO report : reportMap.values()) {
            BigDecimal netChange = report.getTotalImportValue().subtract(report.getTotalExportValue());
            report.setNetChange(netChange);
        }

        return new ArrayList<>(reportMap.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDTO> getTopProducts(
            LocalDate fromDate,
            LocalDate toDate,
            String type,
            Integer limit
    ) {
        LocalDateTime startDateTime = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        int resultLimit = limit != null && limit > 0 ? limit : 10;

        Map<Integer, TopProductDTO> productMap = new HashMap<>();

        if ("import".equalsIgnoreCase(type)) {
            // Get top import products
            List<StockImport> imports = stockImportRepository.findAll().stream()
                    .filter(imp -> !imp.getImportDate().isBefore(startDateTime) &&
                                  !imp.getImportDate().isAfter(endDateTime))
                    .collect(Collectors.toList());

            for (StockImport imp : imports) {
                List<StockImportItem> items = stockImportItemRepository.findByStockImportId(imp.getId());
                for (StockImportItem item : items) {
                    Integer varId = item.getProductVariation().getId();
                    TopProductDTO dto = productMap.computeIfAbsent(varId, id -> {
                        ProductVariation pv = inventoryReportRepository.findById(id).orElse(null);
                        if (pv != null) {
                            return TopProductDTO.builder()
                                    .productVariationId(pv.getId())
                                    .productName(pv.getProduct() != null ? pv.getProduct().getName() : "")
                                    .variationName(pv.getVariationName())
                                    .sku(pv.getSku())
                                    .totalQuantity(0)
                                    .totalValue(BigDecimal.ZERO)
                                    .transactionCount(0)
                                    .build();
                        }
                        return null;
                    });

                    if (dto != null) {
                        dto.setTotalQuantity(dto.getTotalQuantity() + item.getQuantity());
                        BigDecimal itemValue = item.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        dto.setTotalValue(dto.getTotalValue().add(itemValue));
                        dto.setTransactionCount(dto.getTransactionCount() + 1);
                    }
                }
            }
        } else if ("export".equalsIgnoreCase(type)) {
            // Get top export products
            List<StockExport> exports = stockExportRepository.findAll().stream()
                    .filter(exp -> !exp.getExportDate().isBefore(startDateTime) &&
                                  !exp.getExportDate().isAfter(endDateTime))
                    .collect(Collectors.toList());

            for (StockExport exp : exports) {
                List<StockExportItem> items = stockExportItemRepository.findByStockExportId(exp.getId());
                for (StockExportItem item : items) {
                    Integer varId = item.getProductVariation().getId();
                    TopProductDTO dto = productMap.computeIfAbsent(varId, id -> {
                        ProductVariation pv = inventoryReportRepository.findById(id).orElse(null);
                        if (pv != null) {
                            return TopProductDTO.builder()
                                    .productVariationId(pv.getId())
                                    .productName(pv.getProduct() != null ? pv.getProduct().getName() : "")
                                    .variationName(pv.getVariationName())
                                    .sku(pv.getSku())
                                    .totalQuantity(0)
                                    .totalValue(BigDecimal.ZERO)
                                    .transactionCount(0)
                                    .build();
                        }
                        return null;
                    });

                    if (dto != null) {
                        dto.setTotalQuantity(dto.getTotalQuantity() + item.getQuantity());
                        BigDecimal itemValue = item.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        dto.setTotalValue(dto.getTotalValue().add(itemValue));
                        dto.setTransactionCount(dto.getTransactionCount() + 1);
                    }
                }
            }
        }

        return productMap.values().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(TopProductDTO::getTotalQuantity).reversed())
                .limit(resultLimit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryAlertsDTO getInventoryAlerts() {
        List<ProductVariation> outOfStock = inventoryReportRepository.findOutOfStockProducts();
        List<ProductVariation> lowStock = inventoryReportRepository.findLowStockProducts();
        List<ProductVariation> overstock = inventoryReportRepository.findOverstockProducts();

        return InventoryAlertsDTO.builder()
                .outOfStock(outOfStock.stream().map(this::toProductVariationDTO).collect(Collectors.toList()))
                .lowStock(lowStock.stream().map(this::toProductVariationDTO).collect(Collectors.toList()))
                .overstock(overstock.stream().map(this::toProductVariationDTO).collect(Collectors.toList()))
                .build();
    }

    // Helper methods
    private StockBalanceDTO toStockBalanceDTO(ProductVariation pv) {
        BigDecimal avgCost = pv.getAvgCostPrice() != null ? pv.getAvgCostPrice() : BigDecimal.ZERO;
        BigDecimal totalValue = avgCost.multiply(BigDecimal.valueOf(pv.getStockQuantity()));
        
        Product product = pv.getProduct();
        String productName = product != null ? product.getName() : "";

        return StockBalanceDTO.builder()
                .productVariationId(pv.getId())
                .productName(productName)
                .variationName(pv.getVariationName())
                .sku(pv.getSku())
                .stockQuantity(pv.getStockQuantity())
                .avgCostPrice(avgCost)
                .totalStockValue(totalValue)
                .lowStockThreshold(10) // Fixed threshold for now
                .isLowStock(pv.getStockQuantity() <= 10)
                .build();
    }

    private ProductVariationDTO toProductVariationDTO(ProductVariation pv) {
        Product product = pv.getProduct();
        String productName = product != null ? product.getName() : "";

        return ProductVariationDTO.builder()
                .id(pv.getId())
                .productName(productName)
                .variationName(pv.getVariationName())
                .sku(pv.getSku())
                .stockQuantity(pv.getStockQuantity())
                .avgCostPrice(pv.getAvgCostPrice())
                .lowStockThreshold(10)
                .build();
    }

    private LocalDate getReportDate(LocalDate date, String groupBy) {
        if ("month".equalsIgnoreCase(groupBy)) {
            return date.withDayOfMonth(1);
        }
        return date; // Default to daily
    }
}
