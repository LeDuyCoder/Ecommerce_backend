package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Category;
import kiradev.studio.Eommerce.repository.CategoryRepository;
import kiradev.studio.Eommerce.service.Interface.ICategories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService implements ICategories {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public void createCategory(String name, String description) {
        if(categoryRepository.findByname(name) != null) {
            throw new IllegalArgumentException("Category with name " + name + " already exists.");
        } else {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
        }
    }

    @Override
    public void updateCategory(UUID categoryId, String name, String description) {
        Category category = categoryRepository.findByid(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with name " + name + " does not exist.");
        }
        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findByid(categoryId);
        if(category != null){
            categoryRepository.delete(category);
        }
    }

    @Override
    public Category getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findByid(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " does not exist.");
        }
        return category;
    }

    public Category getCategoryByName(String name) {
        Category category = categoryRepository.findByname(name);
        if (category == null) {
            throw new IllegalArgumentException("Category with name " + name + " does not exist.");
        }
        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("No categories found.");
        }
        return categories;
    }
}
