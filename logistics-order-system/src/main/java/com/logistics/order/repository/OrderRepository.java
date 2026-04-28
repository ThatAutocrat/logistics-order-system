package com.logistics.order.repository;

import com.logistics.order.entity.Order;
import com.logistics.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Find all orders by status.
     */
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    /**
     * Find all orders sorted by creation date descending.
     */
    List<Order> findAllByOrderByCreatedAtDesc();

    /**
     * Check if an order already exists with same customer + pickup + delivery.
     * Used for duplicate prevention.
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE " +
           "LOWER(o.customerEmail) = LOWER(:email) AND " +
           "LOWER(o.pickupAddress) = LOWER(:pickup) AND " +
           "LOWER(o.deliveryAddress) = LOWER(:delivery)")
    boolean existsDuplicateOrder(
            @Param("email") String customerEmail,
            @Param("pickup") String pickupAddress,
            @Param("delivery") String deliveryAddress
    );
}
