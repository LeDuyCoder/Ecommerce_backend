package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RateRepository extends JpaRepository<Rate, UUID> {
    Rate findByUserIdAndProductId(UUID userId, UUID productId);

    List<Rate> findByproductId(UUID productId);
}
