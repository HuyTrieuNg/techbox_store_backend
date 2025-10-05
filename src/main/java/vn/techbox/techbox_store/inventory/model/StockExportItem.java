package vn.techbox.techbox_store.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.techbox.techbox_store.product.model.ProductVariation;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_export_items",
    indexes = {
        @Index(name = "idx_export_item_document", columnList = "document_id"),
        @Index(name = "idx_export_item_variation", columnList = "product_variation_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_export_doc_variation", 
                         columnNames = {"document_id", "product_variation_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockExportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private StockExport stockExport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @PrePersist
    @PreUpdate
    protected void validate() {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost price cannot be negative");
        }
    }

    public BigDecimal getTotalValue() {
        return costPrice.multiply(new BigDecimal(quantity));
    }
}
