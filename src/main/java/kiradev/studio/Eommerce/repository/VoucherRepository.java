package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    Optional<Voucher> findByCode(String code);
    Optional<Voucher> findByExpiryDate(LocalDate expiryDate);
}
