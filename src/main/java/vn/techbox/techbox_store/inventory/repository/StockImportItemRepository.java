package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockImportItem;

import java.util.List;

@Repository
public interface StockImportItemRepository extends JpaRepository<StockImportItem, Integer> {
    
    @Query("SELECT sii FROM StockImportItem sii WHERE sii.stockImport.id = :stockImportId")
    List<StockImportItem> findByStockImportId(@Param("stockImportId") Integer stockImportId);
}
