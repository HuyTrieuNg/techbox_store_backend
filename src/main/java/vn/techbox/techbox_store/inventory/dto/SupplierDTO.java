package vn.techbox.techbox_store.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {
    private Integer supplierId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean deleted;
}
