package vn.techbox.techbox_store.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_adjustments", indexes = {
    @Index(name = "idx_adjustment_date", columnList = "adjustment_date"),
    @Index(name = "idx_adjustment_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_code", nullable = false, unique = true, length = 30)
    private String documentCode;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "check_name", length = 255)
    private String checkName;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDateTime adjustmentDate;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "stockAdjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockAdjustmentItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (documentCode == null) {
            documentCode = generateDocumentCode();
        }
        if (adjustmentDate == null) {
            adjustmentDate = LocalDateTime.now();
        }
    }

    private String generateDocumentCode() {
        // Format: ADJ-20241006-XXXX
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timestamp = String.valueOf(System.currentTimeMillis() % 10000);
        return String.format("ADJ-%s-%04d", dateStr, Integer.parseInt(timestamp));
    }

    public void addItem(StockAdjustmentItem item) {
        items.add(item);
        item.setStockAdjustment(this);
    }
}
