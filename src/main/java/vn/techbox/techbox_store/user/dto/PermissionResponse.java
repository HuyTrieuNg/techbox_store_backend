package vn.techbox.techbox_store.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private Integer id;
    private String name;
    private String description;
    private String module;
    private String action;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

