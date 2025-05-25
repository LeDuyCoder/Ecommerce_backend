package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Category;
import kiradev.studio.Eommerce.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Products, UUID> {
    Products findByproductID(UUID productID);
    List<Products> findByshopID(UUID shopID);
    void deleteByproductID(UUID productID);
}
