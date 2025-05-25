package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.Enum.ProductStatus;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.ProductRepository;
import kiradev.studio.Eommerce.service.Interface.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }


    /**
     * Creates a new product with the specified details and associates it with a shop.
     *
     * @param name        the name of the product
     * @param description a brief description of the product
     * @param price       the price of the product
     * @param stock       the available stock for the product
     * @param image       an optional image of the product as a byte array
     * @param shopId      the UUID of the shop to which this product belongs
     */
    @Override
    public void createProduct(String name, String description, double price, int stock, byte[] image, UUID shopId) {
        Products product = new Products();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(image);
        product.setShopID(shopId);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setSold(0);
        product.setCreatedAt(Instant.now().toString());

        productRepository.save(product);
    }

    /**
     * Updates an existing product with the specified details.
     *
     * @param productId   the UUID of the product to update
     * @param name        the new name of the product (can be null)
     * @param description the new description of the product (can be null)
     * @param price       the new price of the product (must be non-negative)
     * @param stock       the new stock quantity of the product (must be non-negative)
     * @param image       an optional new image of the product as a byte array (can be null)
     */
    @Override
    public void updateProduct(UUID productId, String name, String description, double price, int stock, byte[] image) {
        Products product = productRepository.findByproductID(productId);

        if(product!=null){
            if(name != null) {
                product.setName(name);
            }
            if(description != null) {
                product.setDescription(description);
            }
            if(price >= 0) {
                product.setPrice(price);
            }
            if(stock >= 0) {
                product.setStock(stock);
            }
            if(image != null) {
                product.setImage(image);
            }

            productRepository.save(product);
        }
    }


    /**
     * Updates the status of a product identified by its UUID.
     *
     * @param productId the UUID of the product to update
     * @param status    the new status to set for the product
     */
    public void updateProductStatus(UUID productId, ProductStatus status) {
        Products product = productRepository.findByproductID(productId);
        if (product != null) {
            product.setStatus(status);
            productRepository.save(product);
        }
    }

    /**
     * Deletes a product identified by its UUID.
     *
     * @param productId the UUID of the product to delete
     */
    @Override
    public void deleteProduct(UUID productId) {
        Products product = productRepository.findByproductID(productId);
        if (product != null) {
            productRepository.delete(product);
        }
    }

    /**
     * Retrieves a product by its UUID.
     *
     * @param productId the UUID of the product to retrieve
     * @return the product with the specified UUID, or null if not found
     */
    @Override
    public Products getProductById(UUID productId) {
        return productRepository.findByproductID(productId);
    }

    /**
     * Retrieves all products associated with a specific shop ID.
     *
     * @param shopId the UUID of the shop whose products are to be retrieved
     * @return a list of products belonging to the specified shop
     */
    @Override
    public List<Products> getAllProductsByShopID(UUID shopId) {
        return productRepository.findByshopID(shopId);
    }

    /**
     * Retrieves all products available in the system.
     *
     * @return a list of all products
     */
    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }
}
