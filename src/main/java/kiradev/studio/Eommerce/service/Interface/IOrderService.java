package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.Enum.PaymentMethod;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.User;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    void createOrder(User user, float priceTotal);
    void deleteOrder(UUID orderId);
    Order getOrderById(UUID orderId);
    List<Order> getAllOrdersByUserId(UUID userId);
}
