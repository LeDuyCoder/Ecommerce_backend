package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrder(Order order);
    List<Payment> findByStatus(OrderStatus status);
}
