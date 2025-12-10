package vn.techbox.techbox_store.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModulePermissionResponse {
    private String moduleName;
    private String description;
    private List<PermissionResponse> permissions;
    private Integer totalPermissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
