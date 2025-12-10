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
public class ModulePermissionCreateRequest {

    @NotBlank(message = "Module name is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Module name must contain only uppercase letters and underscores")
    @Size(max = 50, message = "Module name must not exceed 50 characters")
    private String moduleName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
