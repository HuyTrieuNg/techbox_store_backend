package vn.techbox.techbox_store.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_imports", indexes = {
    @Index(name = "idx_import_date", columnList = "import_date"),
    @Index(name = "idx_import_user", columnList = "user_id"),
    @Index(name = "idx_import_supplier", columnList = "supplier_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_code", nullable = false, unique = true, length = 30)
    private String documentCode;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "import_date", nullable = false)
    private LocalDateTime importDate;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "total_cost_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCostValue;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "stockImport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockImportItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (documentCode == null) {
            documentCode = generateDocumentCode();
        }
        if (importDate == null) {
            importDate = LocalDateTime.now();
        }
    }

    private String generateDocumentCode() {
        // Format: IMP-20241006-XXXX (XXXX sẽ được generate từ service với sequence)
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        return String.format("IMP-%s-%04d", dateStr, Integer.parseInt(timestamp));
    }

    // Helper method để add item
    public void addItem(StockImportItem item) {
        items.add(item);
        item.setStockImport(this);
    }

    // Tính tổng giá trị
    public void calculateTotalCostValue() {
        this.totalCostValue = items.stream()
            .map(item -> item.getCostPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
