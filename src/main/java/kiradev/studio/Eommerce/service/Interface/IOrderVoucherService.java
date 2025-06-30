package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderVoucher;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Voucher;

import java.util.List;

public interface IOrderVoucherService {
    void applyVoucherToOrder(Voucher voucher, Order order);
    void removeVoucherFromOrder(Order order);
    boolean isVoucherApplied(Voucher voucher, User user);
    List<OrderVoucher> getOrderVouchersByOrder(Order order);
}
