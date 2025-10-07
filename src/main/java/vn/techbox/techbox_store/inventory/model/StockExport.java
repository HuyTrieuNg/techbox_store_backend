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
@Table(name = "stock_exports", indexes = {
    @Index(name = "idx_export_date", columnList = "export_date"),
    @Index(name = "idx_export_user", columnList = "user_id"),
    @Index(name = "idx_export_order", columnList = "order_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_code", nullable = false, unique = true, length = 30)
    private String documentCode;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "export_date", nullable = false)
    private LocalDateTime exportDate;

    @Column(name = "total_cogs_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCogsValue;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "stockExport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockExportItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (documentCode == null) {
            documentCode = generateDocumentCode();
        }
        if (exportDate == null) {
            exportDate = LocalDateTime.now();
        }
    }

    private String generateDocumentCode() {
        // Format: EXP-20241006-XXXX
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        return String.format("EXP-%s-%04d", dateStr, Integer.parseInt(timestamp));
    }

    public void addItem(StockExportItem item) {
        items.add(item);
        item.setStockExport(this);
    }

    public void calculateTotalCogsValue() {
        this.totalCogsValue = items.stream()
            .map(item -> item.getCostPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
