package kiradev.studio.Eommerce.service;

import jakarta.persistence.EntityNotFoundException;
import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.Enum.PaymentMethod;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.OrderRepository;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.Interface.IOrderService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createOrder(User user, PaymentMethod paymentMethod, OrderStatus status, float totalPrice) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(status);
        order.setCreated_at(Instant.now().toString());

        orderRepository.save(order);
    }

    /**
     * Updates the status of an existing order identified by its ID.
     *
     * @param orderId The UUID of the order to be updated.
     * @param status  The new status to set for the order.
     */
    @Override
    public void updateOrder(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));
        order.setStatus(status);

        orderRepository.save(order);
    }

    /**
     * Deletes an order identified by its ID.
     *
     * @param orderId The UUID of the order to be deleted.
     */
    @Override
    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order with ID " + orderId + " does not exist.");
        }
        orderRepository.deleteById(orderId);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId The UUID of the order to retrieve.
     * @return The Order object associated with the given ID.
     * @throws IllegalArgumentException if the order does not exist.
     */
    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));
    }

    /**
     * Retrieves all orders associated with a specific user.
     *
     * @param user The User object whose orders are to be retrieved.
     * @return A list of Order objects associated with the user.
     */
    public List<Order> getAllOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * Retrieves all orders associated with a specific user ID.
     *
     * @param userId The UUID of the user whose orders are to be retrieved.
     * @return A list of Order objects associated with the user ID.
     * @throws EntityNotFoundException if the user with the given ID does not exist.
     */
    @Override
    public List<Order> getAllOrdersByUserId(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + userId + " not found");
        }
        return orderRepository.findByUser(optionalUser.get());
    }

    /**
     * Retrieves all orders from the repository.
     *
     * @return A list of all Order objects.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
