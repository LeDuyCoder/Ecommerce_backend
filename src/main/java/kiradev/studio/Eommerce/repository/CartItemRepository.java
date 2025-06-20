package kiradev.studio.Eommerce.repository;

import jakarta.transaction.Transactional;
import kiradev.studio.Eommerce.entity.Cart;
import kiradev.studio.Eommerce.entity.CartItem;
import kiradev.studio.Eommerce.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, Products product);
    void deleteByCartAndProduct(Cart cart, Products product);
    void deleteByCart(Cart cart);
}

