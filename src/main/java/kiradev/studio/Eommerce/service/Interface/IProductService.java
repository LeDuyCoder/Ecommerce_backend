package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Category;
import kiradev.studio.Eommerce.entity.Products;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    void createProduct(String name, String description, double price, int stock, byte[] image, UUID shopId, List<String> categories);
    void updateProduct(UUID productId, String name, String description, double price, int stock, byte[] image);
    void deleteProduct(UUID productId);
    Products getProductById(UUID productId);
    List<Products> getAllProductsByShopID(UUID shopId);
}
