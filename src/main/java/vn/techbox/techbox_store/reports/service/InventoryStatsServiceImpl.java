package vn.techbox.techbox_store.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.reports.dto.InventoryStatsDTO;
import vn.techbox.techbox_store.reports.dto.StockMovementDTO;
import vn.techbox.techbox_store.reports.repository.InventoryStatsRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryStatsServiceImpl implements InventoryStatsService {

    private final InventoryStatsRepository inventoryStatsRepository;
    
    @Value("${inventory.min-stock-threshold}")
    private int minStockThreshold;

    @Override
    public InventoryStatsDTO getInventoryOverview() {
        log.info("Fetching inventory overview statistics");
        
        BigDecimal totalValue = inventoryStatsRepository.calculateTotalInventoryValue();
        Long totalImports = inventoryStatsRepository.countTotalStockImports();
        Long totalExports = inventoryStatsRepository.countTotalStockExports();
        Integer totalVariations = inventoryStatsRepository.countTotalProductVariations();
        Integer lowStockVariations = inventoryStatsRepository.countLowStockVariations(minStockThreshold);
        
        // Get recent movements (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();
        List<StockMovementDTO> recentMovements = getStockMovements(thirtyDaysAgo, now);
        
        // Limit to 20 most recent
        if (recentMovements.size() > 20) {
            recentMovements = recentMovements.subList(0, 20);
        }
        
        return InventoryStatsDTO.builder()
                .totalInventoryValue(totalValue)
                .totalStockImports(totalImports)
                .totalStockExports(totalExports)
                .totalProductVariations(totalVariations)
                .lowStockVariations(lowStockVariations)
                .recentMovements(recentMovements)
                .build();
    }

    @Override
    public List<StockMovementDTO> getStockMovements(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching stock movements from {} to {}", startDate, endDate);
        
        List<StockMovementDTO> imports = inventoryStatsRepository.findRecentStockImports(startDate, endDate);
        List<StockMovementDTO> exports = inventoryStatsRepository.findRecentStockExports(startDate, endDate);
        
        // Combine and sort by date descending
        List<StockMovementDTO> allMovements = new ArrayList<>();
        allMovements.addAll(imports);
        allMovements.addAll(exports);
        
        allMovements.sort(Comparator.comparing(StockMovementDTO::getTransactionDate).reversed());
        
        return allMovements;
    }
}
