package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findById(UUID id);
    void deleteById(UUID id);
    List<Order> findByUser(User user);
}
