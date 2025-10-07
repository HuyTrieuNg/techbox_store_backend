package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockAdjustment;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Integer> {
}
