package com.ShopSphere.Repository;

import com.ShopSphere.Entity.Order;
import com.ShopSphere.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByStatus(OrderStatus status);


}
