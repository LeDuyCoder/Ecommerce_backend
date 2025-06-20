package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderItem;
import kiradev.studio.Eommerce.entity.Products;

import java.util.List;
import java.util.UUID;

public interface IOrderItemService {
    void addItemToOrder(Order order, Products products, int quantity, float singlePrice);
    void updateItemQuantityInOrder(Order order, Products products, int quantity);
    void removeItemFromOrder(Order order, Products products);
    void clearOrderItems(Order order);
    List<OrderItem> getOrderItemsByOrderId(Order oder);
}
