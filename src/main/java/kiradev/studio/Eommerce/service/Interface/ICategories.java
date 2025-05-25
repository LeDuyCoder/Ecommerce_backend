package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Category;

import java.util.List;
import java.util.UUID;

public interface ICategories {
    void createCategory(String name, String description);
    void updateCategory(UUID categoryId, String name, String description);
    void deleteCategory(UUID categoryId);
    Category getCategoryById(UUID categoryId);
    List<Category> getAllCategories();
}
