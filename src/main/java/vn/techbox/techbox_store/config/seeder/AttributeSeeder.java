package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.product.model.Attribute;
import vn.techbox.techbox_store.product.repository.AttributeRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "development"})
public class AttributeSeeder implements DataSeeder {

    private final AttributeRepository attributeRepository;

    @Override
    public int getOrder() {
        return 4; // Run after Brand, before Product
    }

    @Override
    public boolean shouldSkip() {
        long count = attributeRepository.count();
        if (count > 0) {
            log.info("Attributes already exist ({} found), skipping seeder", count);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void seed() {
        log.info("Starting Attribute seeding...");
        
        List<Attribute> attributes = new ArrayList<>();
        
        // Common attributes for electronic products
        attributes.add(createAttribute("Storage")); // Dung lượng lưu trữ
        attributes.add(createAttribute("Color")); // Màu sắc
        attributes.add(createAttribute("RAM")); // Bộ nhớ RAM
        attributes.add(createAttribute("Screen Size")); // Kích thước màn hình
        attributes.add(createAttribute("Processor")); // Bộ xử lý
        attributes.add(createAttribute("Battery")); // Pin
        attributes.add(createAttribute("Camera")); // Camera
        attributes.add(createAttribute("Operating System")); // Hệ điều hành
        attributes.add(createAttribute("Connectivity")); // Kết nối
        attributes.add(createAttribute("Weight")); // Trọng lượng
        attributes.add(createAttribute("Material")); // Chất liệu
        attributes.add(createAttribute("Warranty")); // Bảo hành
        
        attributeRepository.saveAll(attributes);
        log.info("✓ Created {} attributes", attributes.size());
        log.info("Attribute seeding completed successfully");
    }

    private Attribute createAttribute(String name) {
        return Attribute.builder()
                .name(name)
                .build();
    }
}
