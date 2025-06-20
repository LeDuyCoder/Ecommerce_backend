package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderItem;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.repository.OrderItemRepository;
import kiradev.studio.Eommerce.service.Interface.IOrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService implements IOrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public void addItemToOrder(Order order, Products products, int quantity, float singlePrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(singlePrice);
        orderItem.setOrder(order);
        orderItem.setProducts(products);
        orderItem.setQuantity(quantity);

        orderItemRepository.save(orderItem);
    }

    @Override
    public void updateItemQuantityInOrder(Order order, Products products, int quantity) {

    }

    @Override
    public void removeItemFromOrder(Order order, Products products) {

    }

    @Override
    public void clearOrderItems(Order order) {

    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Order oder) {
        return List.of();
    }
}
