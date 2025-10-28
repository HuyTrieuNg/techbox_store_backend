package vn.techbox.techbox_store.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.order.model.OrderShippingInfo;

import java.util.Optional;

@Repository
public interface OrderShippingInfoRepository extends JpaRepository<OrderShippingInfo, Long> {

    @Query("SELECT osi FROM OrderShippingInfo osi WHERE osi.trackingNumber = :trackingNumber")
    Optional<OrderShippingInfo> findByTrackingNumber(@Param("trackingNumber") String trackingNumber);

    @Query("SELECT osi FROM OrderShippingInfo osi WHERE osi.shippingCity = :city")
    java.util.List<OrderShippingInfo> findByShippingCity(@Param("city") String city);

    @Query("SELECT osi FROM OrderShippingInfo osi WHERE osi.carrier = :carrier")
    java.util.List<OrderShippingInfo> findByCarrier(@Param("carrier") String carrier);
}
