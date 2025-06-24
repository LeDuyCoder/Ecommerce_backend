package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.Enum.PaymentMethod;
import kiradev.studio.Eommerce.Enum.PaymentStatus;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.Payment;
import kiradev.studio.Eommerce.repository.PaymentRepository;
import kiradev.studio.Eommerce.service.Interface.IPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void createPayment(Order order, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setStatus(paymentStatus);
        payment.setPaymentMethod(paymentMethod);
        payment.setCreated_at(Instant.now().toString());
        paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentStatus(Order order, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentMethod(Order order, PaymentMethod paymentMethod) {
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
        payment.setPaymentMethod(paymentMethod);
        paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Order order) {
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
        paymentRepository.delete(payment);
    }

    @Override
    public Payment getPaymentStatus(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
    }

    @Override
    public Payment getPaymentMethod(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
    }

    @Override
    public Payment getPaymentByOrder(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
    }

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
    }

    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    @Override
    public List<Payment> getAllPayment() {
        return paymentRepository.findAll();
    }
}
