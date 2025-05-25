package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Category findByname(String name);
    Category findByid(UUID categoryID);
    void deleteByname(String name);
    void deleteByid(UUID categoryID);
}
