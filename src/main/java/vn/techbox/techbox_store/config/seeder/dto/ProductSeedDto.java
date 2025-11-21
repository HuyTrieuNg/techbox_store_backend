package vn.techbox.techbox_store.config.seeder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductSeedDto {
    private String name;
    private String image;
    private String category;
    private String brand;
    
    @JsonProperty("description_md")
    private String descriptionMd;
    
    private List<VariantSeedDto> variants;
    
    @JsonProperty("common_specs")
    private Map<String, String> commonSpecs;
    
    @Data
    public static class VariantSeedDto {
        private String name;
        private String price;
        private List<String> images;
        private Map<String, String> attributes;
    }
}

