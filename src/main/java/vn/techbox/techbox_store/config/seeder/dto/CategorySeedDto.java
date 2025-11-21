package vn.techbox.techbox_store.config.seeder.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategorySeedDto {
    private String name;
    private String url;
    private List<CategorySeedDto> children;
}

