package vn.techbox.techbox_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techbox.techbox_store.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
