package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.Enum.PaymentMethod;
import kiradev.studio.Eommerce.Enum.PaymentStatus;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.Payment;

import java.util.List;

public interface IPaymentService {
    void createPayment(Order order, PaymentStatus paymentStatus, PaymentMethod paymentMethod);
    void updatePaymentStatus(Order order, PaymentStatus paymentStatus);
    void updatePaymentMethod(Order order, PaymentMethod paymentMethod);
    void deletePayment(Order order);
    Payment getPaymentStatus(Order order);
    Payment getPaymentMethod(Order order);
    Payment getPaymentByOrder(Order order);
    List<Payment> getAllPayment();
}
