package vn.techbox.techbox_store.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionsRequest {
    @NotNull(message = "Role ID is required")
    private Integer roleId;

    @NotEmpty(message = "Permission IDs cannot be empty")
    private Set<Integer> permissionIds;
}

