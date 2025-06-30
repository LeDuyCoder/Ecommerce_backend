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

    /**
     * Adds an item to the order.
     *
     * @param order     The order to which the item is added.
     * @param products  The product being added to the order.
     * @param quantity  The quantity of the product being added.
     * @param singlePrice The price of a single unit of the product.
     */
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

    /**
     * Updates the quantity of an item in the order.
     *
     * @param order     The order containing the item.
     * @param products  The product whose quantity is being updated.
     * @param quantity  The new quantity to set for the product.
     */
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

    /**
     * Updates the price of an item in the order.
     *
     * @param order        The order containing the item.
     * @param quality      The quantity of the product being updated.
     * @param singlePrice  The new price for a single unit of the product.
     */
    public void updateItemPriceInOrder(Order order, int quality, double singlePrice) {
        order.setTotalPrice((float) (order.getTotalPrice() + (quality * singlePrice)));
        if(order.getTotalPrice() < 0) {
            order.setTotalPrice(0f);
        }
        orderRepository.save(order);
    }

    /**
     * Removes an item from the order.
     *
     * @param order     The order from which the item is removed.
     * @param products  The product being removed from the order.
     */
    @Override
    public void removeItemFromOrder(Order order, Products products) {
        OrderItem orderItem = orderItemRepository.findByOrderAndProducts(order, products)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found for the given order and product."));
        orderItemRepository.delete(orderItem);
    }

    /**
     * Clears all items from the order.
     *
     * @param order The order whose items are to be cleared.
     */
    @Override
    public void clearOrderItems(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        if (orderItems.isEmpty()) {
            return; // No items to clear
        }
        orderItemRepository.deleteByOrder(order);
    }

    /**
     * Retrieves all items in a specific order.
     *
     * @param oder The order for which to retrieve items.
     * @return A list of OrderItem entities associated with the order.
     */
    @Override
    public List<OrderItem> getOrderItemsByOrderId(Order oder) {
        return orderItemRepository.findByOrder(oder);
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param id The UUID of the order item to retrieve.
     * @return The OrderItem object associated with the given ID.
     * @throws IllegalArgumentException if the order item does not exist.
     */
    public OrderItem getOrderItemById(UUID id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found for the given ID: " + id));
    }

    /**
     * Retrieves an order item by its associated order and product.
     *
     * @param order   The order containing the item.
     * @param product The product associated with the item.
     * @return The OrderItem object if found, otherwise null.
     */
    public OrderItem getOrderItemByOrderAndProduct(Order order, Products product) {
        return orderItemRepository.findByOrderAndProducts(order, product)
                .orElse(null);
    }
}
