package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedLowStockProductDTO {
    private List<LowStockProductDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
