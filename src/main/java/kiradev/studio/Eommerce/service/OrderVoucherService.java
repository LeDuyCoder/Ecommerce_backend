package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderVoucher;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Voucher;
import kiradev.studio.Eommerce.repository.OrderVoucherRopository;
import kiradev.studio.Eommerce.service.Interface.IOrderVoucherService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderVoucherService implements IOrderVoucherService {
    private final OrderVoucherRopository orderVoucherRopository;

    public OrderVoucherService(OrderVoucherRopository orderVoucherRopository) {
        this.orderVoucherRopository = orderVoucherRopository;
    }

    /**
     * Applies a voucher to an order.
     *
     * @param voucher The voucher to be applied.
     * @param order   The order to which the voucher is applied.
     */
    @Override
    public void applyVoucherToOrder(Voucher voucher, Order order) {
        OrderVoucher orderVoucher = new OrderVoucher();
        orderVoucher.setVoucher(voucher);
        orderVoucher.setOrder(order);

        orderVoucherRopository.save(orderVoucher);
    }

    /**
     * Removes a voucher from an order.
     *
     * @param order The order from which the voucher is removed.
     */
    @Override
    public void removeVoucherFromOrder(Order order) {
        orderVoucherRopository.deleteByOrder(order);
    }

    /**
     * Checks if a voucher is already applied to an order by a specific user.
     *
     * @param voucher The voucher to check.
     * @param user    The user who applied the voucher.
     * @return true if the voucher is applied, false otherwise.
     */
    @Override
    public boolean isVoucherApplied(Voucher voucher, User user) {
        return orderVoucherRopository.existsByVoucherAndUser(voucher, user);
    }

    /**
     * Retrieves all vouchers applied to a specific order.
     *
     * @param order The order for which to retrieve vouchers.
     * @return A list of OrderVoucher entities associated with the order.
     */
    @Override
    public List<OrderVoucher> getOrderVouchersByOrder(Order order) {
        return orderVoucherRopository.findByOrder(order);
    }
}
