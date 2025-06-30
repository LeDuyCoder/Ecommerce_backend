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

    /**
     * Creates a new payment for the given order with the specified payment status and method.
     *
     * @param order          The order for which the payment is being created.
     * @param paymentStatus  The status of the payment (e.g., PENDING, COMPLETED).
     * @param paymentMethod  The method of payment (e.g., CREDIT_CARD, PAYPAL).
     */
    @Override
    public void createPayment(Order order, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setStatus(paymentStatus);
        payment.setPaymentMethod(paymentMethod);
        payment.setCreated_at(Instant.now().toString());
        paymentRepository.save(payment);
    }

    /**
     * Updates the payment status for the given order.
     *
     * @param order          The order for which the payment status is being updated.
     * @param paymentStatus  The new status of the payment.
     */
    @Override
    public void updatePaymentStatus(Order order, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
    }

    /**
     * Updates the payment method for the given order.
     *
     * @param order          The order for which the payment method is being updated.
     * @param paymentMethod  The new payment method to be set.
     */
    @Override
    public void updatePaymentMethod(Order order, PaymentMethod paymentMethod) {
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
        payment.setPaymentMethod(paymentMethod);
        paymentRepository.save(payment);
    }

    /**
     * Deletes the payment associated with the given order.
     *
     * @param order The order for which the payment is to be deleted.
     */
    @Override
    public void deletePayment(Order order) {
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
        paymentRepository.delete(payment);
    }

    /**
     * Retrieves the payment status for the given order.
     *
     * @param order The order for which the payment status is to be retrieved.
     * @return The Payment object associated with the order.
     */
    @Override
    public Payment getPaymentStatus(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
    }

    /**
     * Retrieves the payment method for the given order.
     *
     * @param order The order for which the payment method is to be retrieved.
     * @return The Payment object associated with the order.
     */
    @Override
    public Payment getPaymentMethod(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
    }

    /**
     * Retrieves the payment details for the given order.
     *
     * @param order The order for which the payment details are to be retrieved.
     * @return The Payment object associated with the order.
     */
    @Override
    public Payment getPaymentByOrder(Order order) {
        return paymentRepository.findByOrder(order).orElseThrow(() -> new RuntimeException("Payment not found for order: " + order.getId()));
    }

    /**
     * Retrieves a payment by its ID.
     *
     * @param paymentId The UUID of the payment to retrieve.
     * @return The Payment object associated with the given ID.
     */
    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
    }

    /**
     * Retrieves all payments with pagination support.
     *
     * @param pageable The pagination information.
     * @return A page of Payment objects.
     */
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    /**
     * Retrieves all payments.
     *
     * @return A list of all Payment objects.
     */
    @Override
    public List<Payment> getAllPayment() {
        return paymentRepository.findAll();
    }
}
