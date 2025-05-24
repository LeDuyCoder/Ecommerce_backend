package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {
    Shop findByname(String name);
    Shop findByid(UUID id);
    Shop findByUserID(UUID userID);
    void deleteByid(UUID id);
}

