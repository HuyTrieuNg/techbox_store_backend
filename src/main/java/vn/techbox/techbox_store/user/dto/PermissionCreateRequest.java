package vn.techbox.techbox_store.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateRequest {

    @NotBlank(message = "Permission name is required")
    @Pattern(regexp = "^[A-Z_]+:[A-Z_]+$", message = "Permission name must follow format MODULE:ACTION (e.g., USER:READ)")
    @Size(max = 100, message = "Permission name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
