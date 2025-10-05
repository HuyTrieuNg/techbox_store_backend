package vn.techbox.techbox_store.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.inventory.model.StockExportItem;

import java.util.List;

@Repository
public interface StockExportItemRepository extends JpaRepository<StockExportItem, Integer> {
    
    @Query("SELECT sei FROM StockExportItem sei WHERE sei.stockExport.id = :stockExportId")
    List<StockExportItem> findByStockExportId(@Param("stockExportId") Integer stockExportId);
}
