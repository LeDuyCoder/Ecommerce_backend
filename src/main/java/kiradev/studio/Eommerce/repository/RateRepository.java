package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RateRepository extends JpaRepository<Rate, UUID> {
    Rate findByuserIdAndproductId(UUID userId, UUID productId);

}
