package vn.techbox.techbox_store.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.techbox.techbox_store.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
   
}