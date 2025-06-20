package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderItem;
import kiradev.studio.Eommerce.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProducts(Products product);
    boolean existsByOrderAndProducts(Order order, Products product);
    void deleteByOrder(Order order);
    long countByProducts(Products product);
}
