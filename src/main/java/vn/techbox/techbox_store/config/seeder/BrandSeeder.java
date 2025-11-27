package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.model.Brand;
import vn.techbox.techbox_store.product.repository.BrandRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BrandSeeder implements DataSeeder {

    private final BrandRepository brandRepository;

    @Override
    public void seed() {
        List<Brand> brands = Arrays.asList(
                Brand.builder().name("Apple").build(),
                Brand.builder().name("Samsung").build(),
                Brand.builder().name("Xiaomi").build(),
                Brand.builder().name("OPPO").build(),
                Brand.builder().name("Vivo").build(),
                Brand.builder().name("Realme").build(),
                Brand.builder().name("Dell").build(),
                Brand.builder().name("HP").build(),
                Brand.builder().name("Lenovo").build(),
                Brand.builder().name("Asus").build(),
                Brand.builder().name("Acer").build(),
                Brand.builder().name("MSI").build(),
                Brand.builder().name("Sony").build(),
                Brand.builder().name("JBL").build(),
                Brand.builder().name("Anker").build(),
                Brand.builder().name("Belkin").build(),
                Brand.builder().name("LG").build(),
                Brand.builder().name("Google").build()
        );

        brandRepository.saveAll(brands);
        log.info("✓ Created {} brands", brands.size());
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean shouldSkip() {
        return brandRepository.count() > 0;
    }
}
