package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderItem;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.repository.OrderItemRepository;
import kiradev.studio.Eommerce.repository.OrderRepository;
import kiradev.studio.Eommerce.service.Interface.IOrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderItemService implements IOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void addItemToOrder(Order order, Products products, int quantity, double singlePrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(singlePrice);
        orderItem.setOrder(order);
        orderItem.setProducts(products);
        orderItem.setQuantity(quantity);

        orderItemRepository.save(orderItem);
        updateItemPriceInOrder(order, quantity, products.getPrice());
    }

    @Override
    public void updateItemQuantityInOrder(Order order, Products products, int quantity) {
        OrderItem orderItem = orderItemRepository.findByOrderAndProducts(order, products).get();
        orderItem.setQuantity(orderItem.getQuantity() + quantity);

        if(orderItem.getQuantity() <= 0) {
            orderItemRepository.delete(orderItem);
            updateItemPriceInOrder(order, quantity, products.getPrice());
            return;
        }

        orderItemRepository.save(orderItem);
        updateItemPriceInOrder(order, quantity, products.getPrice());
    }

    public void updateItemPriceInOrder(Order order, int quality, double singlePrice) {
        order.setTotalPrice((float) (order.getTotalPrice() + (quality * singlePrice)));
        if(order.getTotalPrice() < 0) {
            order.setTotalPrice(0f);
        }
        orderRepository.save(order);
    }

    @Override
    public void removeItemFromOrder(Order order, Products products) {
        OrderItem orderItem = orderItemRepository.findByOrderAndProducts(order, products)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found for the given order and product."));
        orderItemRepository.delete(orderItem);
    }

    @Override
    public void clearOrderItems(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        if (orderItems.isEmpty()) {
            return; // No items to clear
        }
        orderItemRepository.deleteByOrder(order);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Order oder) {
        return orderItemRepository.findByOrder(oder);
    }

    public OrderItem getOrderItemById(UUID id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found for the given ID: " + id));
    }

    public OrderItem getOrderItemByOrderAndProduct(Order order, Products product) {
        return orderItemRepository.findByOrderAndProducts(order, product)
                .orElse(null);
    }
}
