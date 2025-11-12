package vn.techbox.techbox_store.config.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.user.dto.AddressCreateRequest;
import vn.techbox.techbox_store.user.model.Address;
import vn.techbox.techbox_store.user.model.User;
import vn.techbox.techbox_store.user.repository.RoleRepository;
import vn.techbox.techbox_store.user.repository.UserRepository;
import vn.techbox.techbox_store.user.service.UserAddressService;

import java.util.Arrays;
import java.util.List;


@Component
@Order(3) // Run after InitialUserSeeder
@Profile({"dev", "development"})
public class AddressSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AddressSeeder.class);

    private final UserRepository userRepository;
    private final UserAddressService userAddressService;

    public AddressSeeder(UserRepository userRepository, UserAddressService userAddressService) {
        this.userRepository = userRepository;
        this.userAddressService = userAddressService;
    }

    @Override
    public void run(String... args) {
        try {
            seedCustomerAddresses();
        } catch (Exception e) {
            log.error("Failed to seed addresses: {}", e.getMessage(), e);
        }
    }

    @Transactional
    protected void seedCustomerAddresses() {
        log.info("Seeding customer addresses...");

        // Seed addresses for customer1@techbox.vn - Multiple addresses
        seedAddressesForCustomer("customer1@techbox.vn", Arrays.asList(
            new AddressCreateRequest(
                "123 Nguyễn Huệ",
                "Phường Bến Nghé",
                "Quận 1",
                "TP. Hồ Chí Minh",
                "70000",
                true,
                "HOME"
            ),
            new AddressCreateRequest(
                "456 Lê Lợi",
                "Phường Phạm Ngũ Lão",
                "Quận 1",
                "TP. Hồ Chí Minh",
                "70000",
                false,
                "WORK"
            )
        ));

        // Seed addresses for customer2@techbox.vn - Multiple addresses
        seedAddressesForCustomer("customer2@techbox.vn", Arrays.asList(
            new AddressCreateRequest(
                "789 Trần Hưng Đạo",
                "Phường Cầu Kho",
                "Quận 1",
                "TP. Hồ Chí Minh",
                "70000",
                true,
                "HOME"
            ),
            new AddressCreateRequest(
                "321 Võ Văn Tần",
                "Phường 6",
                "Quận 3",
                "TP. Hồ Chí Minh",
                "70000",
                false,
                "WORK"
            ),
            new AddressCreateRequest(
                "654 Nguyễn Thị Minh Khai",
                "Phường Đa Kao",
                "Quận 1",
                "TP. Hồ Chí Minh",
                "70000",
                false,
                "OTHER"
            )
        ));

        // Seed addresses for customer3@techbox.vn - Single address
        seedAddressesForCustomer("customer3@techbox.vn", Arrays.asList(
            new AddressCreateRequest(
                "147 Pasteur",
                "Phường Võ Thị Sáu",
                "Quận 3",
                "TP. Hồ Chí Minh",
                "70000",
                true,
                "HOME"
            )
        ));

        // Seed addresses for customer4@techbox.vn - Multiple addresses
        seedAddressesForCustomer("customer4@techbox.vn", Arrays.asList(
            new AddressCreateRequest(
                "258 Cách Mạng Tháng 8",
                "Phường 10",
                "Quận 3",
                "TP. Hồ Chí Minh",
                "70000",
                true,
                "HOME"
            ),
            new AddressCreateRequest(
                "369 Nguyễn Đình Chiểu",
                "Phường 5",
                "Quận 3",
                "TP. Hồ Chí Minh",
                "70000",
                false,
                "WORK"
            )
        ));

        // Seed addresses for customer5@techbox.vn - Single address in Ha Noi
        seedAddressesForCustomer("customer5@techbox.vn", Arrays.asList(
            new AddressCreateRequest(
                "36 Phố Hàng Trống",
                "Phường Hàng Trống",
                "Quận Hoàn Kiếm",
                "TP. Hà Nội",
                "10000",
                true,
                "HOME"
            )
        ));

        log.info("Customer addresses seeding completed");
    }

    private void seedAddressesForCustomer(String email, List<AddressCreateRequest> addresses) {
        var userOpt = userRepository.findByAccountEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Customer with email {} not found, skipping address seeding", email);
            return;
        }

        User user = userOpt.get();

        // Check if user already has addresses
        List<Address> existingAddresses = userAddressService.getUserAddresses(user.getId());
        if (!existingAddresses.isEmpty()) {
            log.debug("Customer {} already has {} addresses, skipping", email, existingAddresses.size());
            return;
        }

        int addressCount = 0;
        for (AddressCreateRequest addressReq : addresses) {
            try {
                userAddressService.createAddress(user.getId(), addressReq);
                addressCount++;
                log.debug("Created address for customer {}: {} {}, {}, {}",
                    email, addressReq.streetAddress(), addressReq.ward(),
                    addressReq.district(), addressReq.city());
            } catch (Exception e) {
                log.error("Failed to create address for customer {}: {}", user.getAccount().getEmail(), e.getMessage());
            }
        }
        log.info("Created {} addresses for customer {}", addressCount, email);
    }
}
