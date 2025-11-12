package vn.techbox.techbox_store.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.product.model.Category;
import vn.techbox.techbox_store.product.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategorySeeder implements DataSeeder {

    private final CategoryRepository categoryRepository;

    @Override
    public void seed() {
        // Create parent categories first (Level 1)
        List<Category> parentCategories = Arrays.asList(
                Category.builder()
                        .name("Điện thoại")
                        .build(),
                
                Category.builder()
                        .name("Laptop")
                        .build(),
                
                Category.builder()
                        .name("Tablet")
                        .build(),
                
                Category.builder()
                        .name("Tai nghe")
                        .build(),
                
                Category.builder()
                        .name("Đồng hồ thông minh")
                        .build(),
                
                Category.builder()
                        .name("Phụ kiện")
                        .build(),
                
                Category.builder()
                        .name("Loa")
                        .build(),
                
                Category.builder()
                        .name("PC & Màn hình")
                        .build()
        );

        categoryRepository.saveAll(parentCategories);
        log.info("✓ Created {} parent categories (Level 1)", parentCategories.size());

        // Create child categories (Level 2)
        List<Category> childCategories = new ArrayList<>();

        // Child categories for "Điện thoại"
        Category phoneCategory = categoryRepository.findByName("Điện thoại").orElse(null);
        if (phoneCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("iPhone").parentCategoryId(phoneCategory.getId()).build(),
                    Category.builder().name("Samsung Galaxy").parentCategoryId(phoneCategory.getId()).build(),
                    Category.builder().name("Xiaomi").parentCategoryId(phoneCategory.getId()).build(),
                    Category.builder().name("OPPO").parentCategoryId(phoneCategory.getId()).build(),
                    Category.builder().name("Vivo").parentCategoryId(phoneCategory.getId()).build(),
                    Category.builder().name("Realme").parentCategoryId(phoneCategory.getId()).build(),
                    Category.builder().name("Nokia").parentCategoryId(phoneCategory.getId()).build()
            ));
        }

        // Child categories for "Laptop"
        Category laptopCategory = categoryRepository.findByName("Laptop").orElse(null);
        if (laptopCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("Laptop Gaming").parentCategoryId(laptopCategory.getId()).build(),
                    Category.builder().name("Laptop Văn phòng").parentCategoryId(laptopCategory.getId()).build(),
                    Category.builder().name("Laptop Đồ họa").parentCategoryId(laptopCategory.getId()).build(),
                    Category.builder().name("Laptop Mỏng nhẹ").parentCategoryId(laptopCategory.getId()).build(),
                    Category.builder().name("Laptop Cao cấp").parentCategoryId(laptopCategory.getId()).build(),
                    Category.builder().name("Macbook").parentCategoryId(laptopCategory.getId()).build()
            ));
        }

        // Child categories for "Tablet"
        Category tabletCategory = categoryRepository.findByName("Tablet").orElse(null);
        if (tabletCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("iPad").parentCategoryId(tabletCategory.getId()).build(),
                    Category.builder().name("Samsung Galaxy Tab").parentCategoryId(tabletCategory.getId()).build(),
                    Category.builder().name("Xiaomi Pad").parentCategoryId(tabletCategory.getId()).build(),
                    Category.builder().name("Tablet Android").parentCategoryId(tabletCategory.getId()).build()
            ));
        }

        // Child categories for "Tai nghe"
        Category headphoneCategory = categoryRepository.findByName("Tai nghe").orElse(null);
        if (headphoneCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("Tai nghe Bluetooth").parentCategoryId(headphoneCategory.getId()).build(),
                    Category.builder().name("Tai nghe có dây").parentCategoryId(headphoneCategory.getId()).build(),
                    Category.builder().name("Tai nghe True Wireless").parentCategoryId(headphoneCategory.getId()).build(),
                    Category.builder().name("Tai nghe Gaming").parentCategoryId(headphoneCategory.getId()).build(),
                    Category.builder().name("Tai nghe chụp tai").parentCategoryId(headphoneCategory.getId()).build()
            ));
        }

        // Child categories for "Đồng hồ thông minh"
        Category smartwatchCategory = categoryRepository.findByName("Đồng hồ thông minh").orElse(null);
        if (smartwatchCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("Apple Watch").parentCategoryId(smartwatchCategory.getId()).build(),
                    Category.builder().name("Samsung Galaxy Watch").parentCategoryId(smartwatchCategory.getId()).build(),
                    Category.builder().name("Xiaomi Watch").parentCategoryId(smartwatchCategory.getId()).build(),
                    Category.builder().name("Vòng đeo tay thông minh").parentCategoryId(smartwatchCategory.getId()).build()
            ));
        }

        // Child categories for "Phụ kiện"
        Category accessoryCategory = categoryRepository.findByName("Phụ kiện").orElse(null);
        if (accessoryCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("Sạc & Cáp").parentCategoryId(accessoryCategory.getId()).build(),
                    Category.builder().name("Sạc dự phòng").parentCategoryId(accessoryCategory.getId()).build(),
                    Category.builder().name("Ốp lưng & Bao da").parentCategoryId(accessoryCategory.getId()).build(),
                    Category.builder().name("Chuột & Bàn phím").parentCategoryId(accessoryCategory.getId()).build(),
                    Category.builder().name("Thẻ nhớ & USB").parentCategoryId(accessoryCategory.getId()).build(),
                    Category.builder().name("Miếng dán màn hình").parentCategoryId(accessoryCategory.getId()).build(),
                    Category.builder().name("Giá đỡ & Kẹp điện thoại").parentCategoryId(accessoryCategory.getId()).build()
            ));
        }

        // Child categories for "Loa"
        Category speakerCategory = categoryRepository.findByName("Loa").orElse(null);
        if (speakerCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("Loa Bluetooth").parentCategoryId(speakerCategory.getId()).build(),
                    Category.builder().name("Loa Thông minh").parentCategoryId(speakerCategory.getId()).build(),
                    Category.builder().name("Loa Máy tính").parentCategoryId(speakerCategory.getId()).build(),
                    Category.builder().name("Loa Karaoke").parentCategoryId(speakerCategory.getId()).build()
            ));
        }

        // Child categories for "PC & Màn hình"
        Category pcCategory = categoryRepository.findByName("PC & Màn hình").orElse(null);
        if (pcCategory != null) {
            childCategories.addAll(Arrays.asList(
                    Category.builder().name("Màn hình máy tính").parentCategoryId(pcCategory.getId()).build(),
                    Category.builder().name("PC Gaming").parentCategoryId(pcCategory.getId()).build(),
                    Category.builder().name("PC Văn phòng").parentCategoryId(pcCategory.getId()).build(),
                    Category.builder().name("Linh kiện máy tính").parentCategoryId(pcCategory.getId()).build(),
                    Category.builder().name("iMac & Mac Mini").parentCategoryId(pcCategory.getId()).build()
            ));
        }

        if (!childCategories.isEmpty()) {
            categoryRepository.saveAll(childCategories);
            log.info("✓ Created {} child categories (Level 2)", childCategories.size());
        }

        // Create grandchild categories (Level 3)
        List<Category> grandchildCategories = new ArrayList<>();

        // Level 3 for iPhone
        Category iphoneCategory = categoryRepository.findByName("iPhone").orElse(null);
        if (iphoneCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("iPhone 15 Series").parentCategoryId(iphoneCategory.getId()).build(),
                    Category.builder().name("iPhone 14 Series").parentCategoryId(iphoneCategory.getId()).build(),
                    Category.builder().name("iPhone 13 Series").parentCategoryId(iphoneCategory.getId()).build(),
                    Category.builder().name("iPhone 12 Series").parentCategoryId(iphoneCategory.getId()).build(),
                    Category.builder().name("iPhone 11 Series").parentCategoryId(iphoneCategory.getId()).build()
            ));
        }

        // Level 3 for Samsung Galaxy
        Category samsungCategory = categoryRepository.findByName("Samsung Galaxy").orElse(null);
        if (samsungCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Galaxy S Series").parentCategoryId(samsungCategory.getId()).build(),
                    Category.builder().name("Galaxy Z Series").parentCategoryId(samsungCategory.getId()).build(),
                    Category.builder().name("Galaxy A Series").parentCategoryId(samsungCategory.getId()).build(),
                    Category.builder().name("Galaxy M Series").parentCategoryId(samsungCategory.getId()).build()
            ));
        }

        // Level 3 for Xiaomi
        Category xiaomiPhoneCategory = categoryRepository.findByName("Xiaomi").orElse(null);
        if (xiaomiPhoneCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Xiaomi Series").parentCategoryId(xiaomiPhoneCategory.getId()).build(),
                    Category.builder().name("Redmi Series").parentCategoryId(xiaomiPhoneCategory.getId()).build(),
                    Category.builder().name("Poco Series").parentCategoryId(xiaomiPhoneCategory.getId()).build()
            ));
        }

        // Level 3 for Laptop Gaming
        Category laptopGamingCategory = categoryRepository.findByName("Laptop Gaming").orElse(null);
        if (laptopGamingCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Gaming RTX 4090").parentCategoryId(laptopGamingCategory.getId()).build(),
                    Category.builder().name("Gaming RTX 4080").parentCategoryId(laptopGamingCategory.getId()).build(),
                    Category.builder().name("Gaming RTX 4070").parentCategoryId(laptopGamingCategory.getId()).build(),
                    Category.builder().name("Gaming RTX 4060").parentCategoryId(laptopGamingCategory.getId()).build(),
                    Category.builder().name("Gaming RTX 4050").parentCategoryId(laptopGamingCategory.getId()).build()
            ));
        }

        // Level 3 for Macbook
        Category macbookCategory = categoryRepository.findByName("Macbook").orElse(null);
        if (macbookCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("MacBook Pro M3").parentCategoryId(macbookCategory.getId()).build(),
                    Category.builder().name("MacBook Pro M2").parentCategoryId(macbookCategory.getId()).build(),
                    Category.builder().name("MacBook Air M3").parentCategoryId(macbookCategory.getId()).build(),
                    Category.builder().name("MacBook Air M2").parentCategoryId(macbookCategory.getId()).build(),
                    Category.builder().name("MacBook Air M1").parentCategoryId(macbookCategory.getId()).build()
            ));
        }

        // Level 3 for iPad
        Category ipadCategory = categoryRepository.findByName("iPad").orElse(null);
        if (ipadCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("iPad Pro").parentCategoryId(ipadCategory.getId()).build(),
                    Category.builder().name("iPad Air").parentCategoryId(ipadCategory.getId()).build(),
                    Category.builder().name("iPad Gen").parentCategoryId(ipadCategory.getId()).build(),
                    Category.builder().name("iPad Mini").parentCategoryId(ipadCategory.getId()).build()
            ));
        }

        // Level 3 for Tai nghe True Wireless
        Category twsCategory = categoryRepository.findByName("Tai nghe True Wireless").orElse(null);
        if (twsCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("AirPods").parentCategoryId(twsCategory.getId()).build(),
                    Category.builder().name("Galaxy Buds").parentCategoryId(twsCategory.getId()).build(),
                    Category.builder().name("Redmi Buds").parentCategoryId(twsCategory.getId()).build(),
                    Category.builder().name("OPPO Enco").parentCategoryId(twsCategory.getId()).build()
            ));
        }

        // Level 3 for Apple Watch
        Category appleWatchCategory = categoryRepository.findByName("Apple Watch").orElse(null);
        if (appleWatchCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Apple Watch Ultra").parentCategoryId(appleWatchCategory.getId()).build(),
                    Category.builder().name("Apple Watch Series 9").parentCategoryId(appleWatchCategory.getId()).build(),
                    Category.builder().name("Apple Watch SE").parentCategoryId(appleWatchCategory.getId()).build()
            ));
        }

        // Level 3 for Sạc & Cáp
        Category chargerCategory = categoryRepository.findByName("Sạc & Cáp").orElse(null);
        if (chargerCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Cáp Lightning").parentCategoryId(chargerCategory.getId()).build(),
                    Category.builder().name("Cáp USB-C").parentCategoryId(chargerCategory.getId()).build(),
                    Category.builder().name("Cáp Micro USB").parentCategoryId(chargerCategory.getId()).build(),
                    Category.builder().name("Củ sạc nhanh").parentCategoryId(chargerCategory.getId()).build(),
                    Category.builder().name("Sạc không dây").parentCategoryId(chargerCategory.getId()).build()
            ));
        }

        // Level 3 for Chuột & Bàn phím
        Category mouseKeyboardCategory = categoryRepository.findByName("Chuột & Bàn phím").orElse(null);
        if (mouseKeyboardCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Chuột Gaming").parentCategoryId(mouseKeyboardCategory.getId()).build(),
                    Category.builder().name("Chuột Văn phòng").parentCategoryId(mouseKeyboardCategory.getId()).build(),
                    Category.builder().name("Bàn phím Gaming").parentCategoryId(mouseKeyboardCategory.getId()).build(),
                    Category.builder().name("Bàn phím Văn phòng").parentCategoryId(mouseKeyboardCategory.getId()).build(),
                    Category.builder().name("Bàn phím cơ").parentCategoryId(mouseKeyboardCategory.getId()).build()
            ));
        }

        // Level 3 for Màn hình máy tính
        Category monitorCategory = categoryRepository.findByName("Màn hình máy tính").orElse(null);
        if (monitorCategory != null) {
            grandchildCategories.addAll(Arrays.asList(
                    Category.builder().name("Màn hình Gaming").parentCategoryId(monitorCategory.getId()).build(),
                    Category.builder().name("Màn hình văn phòng").parentCategoryId(monitorCategory.getId()).build(),
                    Category.builder().name("Màn hình đồ họa").parentCategoryId(monitorCategory.getId()).build(),
                    Category.builder().name("Màn hình cong").parentCategoryId(monitorCategory.getId()).build()
            ));
        }

        if (!grandchildCategories.isEmpty()) {
            categoryRepository.saveAll(grandchildCategories);
            log.info("✓ Created {} grandchild categories (Level 3)", grandchildCategories.size());
        }

        log.info("✓ Total created {} categories across 3 levels",
                parentCategories.size() + childCategories.size() + grandchildCategories.size());
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean shouldSkip() {
        return categoryRepository.count() > 0;
    }
}
