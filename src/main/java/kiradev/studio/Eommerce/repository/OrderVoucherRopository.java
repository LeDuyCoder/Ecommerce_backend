package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderVoucher;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderVoucherRopository extends JpaRepository<OrderVoucher, UUID> {
    List<OrderVoucher> findByOrder(Order order);
    List<OrderVoucher> findByVoucher(Voucher voucher);
    void deleteByOrder(Order order);
    @Query("SELECT CASE WHEN COUNT(ov) > 0 THEN true ELSE false END " +
            "FROM OrderVoucher ov WHERE ov.voucher = :voucher AND ov.order.user = :user")
    boolean existsByVoucherAndUser(@Param("voucher") Voucher voucher, @Param("user") User user);

}
