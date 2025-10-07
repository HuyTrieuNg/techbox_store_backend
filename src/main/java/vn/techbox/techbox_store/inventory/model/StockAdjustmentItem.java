package vn.techbox.techbox_store.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_adjustment_items",
    indexes = {
        @Index(name = "idx_adjustment_item_document", columnList = "document_id"),
        @Index(name = "idx_adjustment_item_variation", columnList = "product_variation_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_adjustment_doc_variation", 
                         columnNames = {"document_id", "product_variation_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private StockAdjustment stockAdjustment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    @Column(name = "system_qty", nullable = false)
    private Integer systemQty;

    @Column(name = "real_qty", nullable = false)
    private Integer realQty;

    @Column(name = "diff_qty", nullable = false)
    private Integer diffQty;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "diff_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal diffValue;

    @Column(name = "note", length = 255)
    private String note;

    @PrePersist
    @PreUpdate
    protected void calculateDifference() {
        // Tự động tính chênh lệch
        this.diffQty = this.realQty - this.systemQty;
        this.diffValue = this.costPrice.multiply(new BigDecimal(this.diffQty));
    }

    // Helper methods
    public boolean hasDiscrepancy() {
        return diffQty != 0;
    }

    public boolean isOverstock() {
        return diffQty > 0;
    }

    public boolean isShortage() {
        return diffQty < 0;
    }
}
