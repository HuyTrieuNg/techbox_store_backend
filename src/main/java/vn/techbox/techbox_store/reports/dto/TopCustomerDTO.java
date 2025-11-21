package vn.techbox.techbox_store.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private Long totalOrders;
    private Double totalSpent;
    private LocalDateTime lastOrderDate;
}
