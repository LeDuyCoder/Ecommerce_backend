package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Category;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.repository.CategoryRepository;
import kiradev.studio.Eommerce.service.Interface.ICategories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService implements ICategories {
    private final CategoryRepository categoryRepository;

    /**
     * Constructor for CategoryService.
     *
     * @param categoryRepository the repository for managing categories
     */
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new category with the specified name and description.
     *
     * @param name        the name of the category
     * @param description a brief description of the category
     */
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

    /**
     * Updates an existing category identified by its ID.
     *
     * @param categoryId  the UUID of the category to update
     * @param name        the new name for the category (can be null)
     * @param description the new description for the category (can be null)
     */
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

    /**
     * Deletes a category identified by its ID.
     *
     * @param categoryId the UUID of the category to delete
     */
    @Override
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findByid(categoryId);
        if(category != null){
            categoryRepository.delete(category);
        }
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId the UUID of the category to retrieve
     * @return the Category object if found
     * @throws IllegalArgumentException if the category does not exist
     */
    @Override
    public Category getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findByid(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " does not exist.");
        }
        return category;
    }

    /**
     * Retrieves a category by its name.
     *
     * @param name the name of the category to retrieve
     * @return the Category object if found
     * @throws IllegalArgumentException if the category does not exist
     */
    public Category getCategoryByName(String name) {
        Category category = categoryRepository.findByname(name);
        if (category == null) {
            throw new IllegalArgumentException("Category with name " + name + " does not exist.");
        }
        return category;
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories
     * @throws IllegalArgumentException if no categories are found
     */
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("No categories found.");
        }
        return categories;
    }

    /**
     * Retrieves all products associated with a specific category name.
     *
     * @param categoryName the name of the category whose products are to be retrieved
     * @return a list of products associated with the specified category
     * @throws IllegalArgumentException if the category does not exist
     */
    public List<Products> getProductsByCategoryName(String categoryName) {
        Category category = categoryRepository.findByname(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Category with name " + categoryName + " does not exist.");
        }
        return category.getProducts();
    }
}
